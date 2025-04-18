from fastapi import FastAPI, UploadFile, File
from fastapi.responses import JSONResponse
import subprocess
import os
import json
import shutil
import logging
from typing import Optional, Dict, Any
import tempfile
from fastapi.middleware.cors import CORSMiddleware
import cv2
import numpy as np
import requests
from pydantic import BaseModel

# Настройка логирования
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('app.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

app = FastAPI(title="LLaVA API", description="API для работы с LLaVA моделью")

# Добавляем CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Конфигурация путей
LLAVA_CLI_PATH = "./scripts/model_photo/llama.cpp/build/bin/llama-llava-cli"
MODEL_PATH = "./scripts/model_photo/model_llm/gemma-3-12b-it-Q4_K_M.gguf"
MMPROJ_PATH = "./scripts/model_photo/model_llm/mmproj-model-f16.gguf"
DEFAULT_PROMPT_PATH = "./scripts/model_photo/model_llm/prompt.txt"


# Глобальные переменные для хранения состояния
model_initialized = False

class ImageURL(BaseModel):
    url: str

def process_table_image(image_path: str, output_path: str):
    """Обработка изображения таблицы"""
    # Чтение изображения
    img = cv2.imread(image_path)
    if img is None:
        raise ValueError(f"Не удалось прочитать изображение: {image_path}")

    # Получаем размеры изображения
    height, width = img.shape[:2]
    
    # Преобразование в оттенки серого
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    
    # Улучшаем контраст изображения
    clahe = cv2.createCLAHE(clipLimit=2.0, tileGridSize=(8,8))
    gray = clahe.apply(gray)
    
    # Уменьшаем шум
    gray = cv2.GaussianBlur(gray, (3, 3), 0)
    
    # Применяем адаптивный порог
    thresh = cv2.adaptiveThreshold(gray, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
                                 cv2.THRESH_BINARY_INV, 15, 3)
    
    # Создаем ядра для морфологических операций
    vertical_kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (1, height//25))
    horizontal_kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (width//25, 1))

    # Находим линии
    vertical_lines = cv2.morphologyEx(thresh, cv2.MORPH_OPEN, vertical_kernel)
    vertical_lines = cv2.dilate(vertical_lines, None, iterations=2)
    horizontal_lines = cv2.morphologyEx(thresh, cv2.MORPH_OPEN, horizontal_kernel)
    horizontal_lines = cv2.dilate(horizontal_lines, None, iterations=2)

    # Обработка горизонтальных линий
    horizontal_contours, _ = cv2.findContours(horizontal_lines, cv2.RETR_EXTERNAL,
                                            cv2.CHAIN_APPROX_SIMPLE)
    horizontal_contours = sorted([cnt for cnt in horizontal_contours 
                                if cv2.boundingRect(cnt)[2] > width * 0.5],
                               key=lambda x: cv2.boundingRect(x)[1])

    # Определение границ верхних строк
    if len(horizontal_contours) > 2:
        _, y_top, _, _ = cv2.boundingRect(horizontal_contours[0])
        _, y_second_row_bottom, _, _ = cv2.boundingRect(horizontal_contours[2])
    else:
        y_top = 0
        y_second_row_bottom = height // 8

    # Обработка вертикальных линий
    contours, _ = cv2.findContours(vertical_lines, cv2.RETR_EXTERNAL,
                                  cv2.CHAIN_APPROX_SIMPLE)
    vertical_contours = []
    for cnt in contours:
        x, y, w, h = cv2.boundingRect(cnt)
        if h > height * 0.4 and w < width * 0.03:
            vertical_contours.append(cnt)

    vertical_contours = sorted(vertical_contours, key=lambda x: cv2.boundingRect(x)[0])

    if len(vertical_contours) < 4:
        raise ValueError("Не удалось обнаружить достаточно столбцов в таблице")

    # Создание маски
    mask = np.zeros_like(img)
    
    # Заполнение верхних строк
    cv2.rectangle(mask, (0, max(0, y_top - 15)), 
                 (width, min(height, y_second_row_bottom + 15)), 
                 (255, 255, 255), -1)
    
    # Обработка первого столбца
    x1, y1, w1, h1 = cv2.boundingRect(vertical_contours[0])
    next_x = cv2.boundingRect(vertical_contours[1])[0] if len(vertical_contours) > 1 else x1 + w1 * 2
    cv2.rectangle(mask, (max(0, x1 - 15), y_second_row_bottom), 
                 (min(width, next_x + 15), height), (255, 255, 255), -1)

    # Обработка правых столбцов
    if len(vertical_contours) >= 4:
        last_columns = vertical_contours[-4:]
        column_widths = []
        column_positions = []
        
        for col in last_columns:
            x, _, w, _ = cv2.boundingRect(col)
            column_widths.append(w)
            column_positions.append(x)
        
        intervals = [column_positions[i+1] - (column_positions[i] + column_widths[i])
                    for i in range(len(column_positions)-1)]
        avg_interval = sum(intervals) / len(intervals) if intervals else 20
        
        x_third_last = column_positions[-3]
        x_last = column_positions[-1]
        w_last = column_widths[-1]
        
        left_padding = min(int(avg_interval * 0.8), 20)
        right_padding = min(int(avg_interval * 0.8), 20)
        
        cv2.rectangle(mask, 
                     (max(0, x_third_last - left_padding), y_second_row_bottom),
                     (min(width, x_last + w_last + right_padding), height),
                     (255, 255, 255), -1)

    # Применение маски и обрезка
    result = cv2.bitwise_and(img, mask)
    gray_result = cv2.cvtColor(result, cv2.COLOR_BGR2GRAY)
    _, binary = cv2.threshold(gray_result, 1, 255, cv2.THRESH_BINARY)
    contours, _ = cv2.findContours(binary, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    
    if contours:
        x_min = width
        x_max = 0
        y_min = height
        y_max = 0
        
        for cnt in contours:
            x, y, w, h = cv2.boundingRect(cnt)
            x_min = min(x_min, x)
            x_max = max(x_max, x + w)
            y_min = min(y_min, y)
            y_max = max(y_max, y + h)
        
        x_min = max(0, x_min - 10)
        x_max = min(width, x_max + 10)
        y_min = max(0, y_min - 10)
        y_max = min(height, y_max + 10)
        
        result = result[y_min:y_max, x_min:x_max]

    cv2.imwrite(output_path, result)
    return True

def read_prompt_content() -> str:
    """Читает содержимое промпта из файла"""
    try:
        with open(DEFAULT_PROMPT_PATH, 'r', encoding='utf-8') as f:
            return f.read()
    except Exception as e:
        logger.error(f"Ошибка при чтении промпта: {str(e)}")
        raise

def run_llava(image_path: str) -> dict:
    """Запускает LLaVA модель и возвращает результат в формате JSON"""
    try:
        logger.info(f"Запуск LLaVA с изображением: {image_path}")
        
        # Установка переменной окружения
        os.environ["GGML_METAL"] = "0"
        
        # Формирование команды
        cmd = [
            LLAVA_CLI_PATH,
            "-m", MODEL_PATH,
            "--mmproj", MMPROJ_PATH,
            "-f", DEFAULT_PROMPT_PATH,
            "--image", image_path,
            "-c", "2500",
            "-n", "50000",
            "-t", "8",
            "--temp", "0.1",
            "--repeat-penalty", "1.1"
        ]
        
        logger.debug(f"Выполняемая команда: {' '.join(cmd)}")
        
        # Запуск процесса
        result = subprocess.run(cmd, capture_output=True, text=True)
        
        if result.returncode != 0:
            error_msg = f"Ошибка при выполнении LLaVA: {result.stderr}"
            logger.error(error_msg)
            raise Exception(error_msg)
        
        logger.info("Получен вывод от модели:")
        logger.info("=" * 50)
        logger.info(result.stdout)
        logger.info("=" * 50)
        
        try:
            json_result = json.loads(result.stdout)
            logger.info("Успешно получен JSON ответ от модели")
            return json_result
        except json.JSONDecodeError as e:
            logger.warning(f"Не удалось преобразовать вывод в JSON. Ошибка: {str(e)}")
            logger.warning("Попытка найти JSON в выводе...")
            
            try:
                json_start = result.stdout.find('[')
                json_end = result.stdout.rfind(']') + 1
                
                if json_start != -1 and json_end != -1:
                    json_str = result.stdout[json_start:json_end]
                    logger.info("Найден JSON массив:")
                    logger.info(json_str)
                    
                    json_result = json.loads(json_str)
                    logger.info(f"Успешно получен массив из {len(json_result)} элементов")
                    return json_result
                
                obj_start = result.stdout.find('{')
                obj_end = result.stdout.rfind('}') + 1
                
                if obj_start != -1 and obj_end != -1:
                    json_str = result.stdout[obj_start:obj_end]
                    logger.info("Найден JSON объект:")
                    logger.info(json_str)
                    
                    json_result = json.loads(json_str)
                    logger.info("Успешно получен JSON объект")
                    return json_result
                
                logger.warning("Не удалось найти JSON структуру в выводе")
                
            except json.JSONDecodeError as e2:
                logger.warning(f"Не удалось преобразовать найденную структуру в JSON. Ошибка: {str(e2)}")
            
            return {
                "error": "json_parse_error",
                "details": {
                    "raw_output": result.stdout,
                    "initial_error": str(e),
                    "output_length": len(result.stdout),
                    "contains_json_array": '[' in result.stdout and ']' in result.stdout,
                    "contains_json_object": '{' in result.stdout and '}' in result.stdout
                }
            }
            
    except Exception as e:
        error_msg = f"Ошибка в run_llava: {str(e)}"
        logger.error(error_msg)
        return {"error": error_msg}

async def initialize_model():
    """Инициализация модели"""
    global model_initialized
    
    if not model_initialized:
        try:
            logger.info("Начало инициализации модели...")
            
            # Проверка наличия необходимых файлов
            for file_path in [LLAVA_CLI_PATH, MODEL_PATH, MMPROJ_PATH, DEFAULT_PROMPT_PATH]:
                if not os.path.exists(file_path):
                    error_msg = f"Файл не найден: {file_path}"
                    logger.error(error_msg)
                    raise FileNotFoundError(error_msg)
            
            model_initialized = True
            logger.info("Модель успешно инициализирована")
            
        except Exception as e:
            error_msg = f"Ошибка при инициализации модели: {str(e)}"
            logger.error(error_msg)
            raise Exception(error_msg)

