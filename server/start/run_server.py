import logging
from fastapi import FastAPI, UploadFile, File
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from datetime import datetime
from pathlib import Path
from main import classify, generate_abbreviations
from typing import Optional
import os
import tempfile
import shutil
import requests
from scripts.model_photo.app import run_llava, process_table_image, initialize_model

app = FastAPI()
abbreviations = generate_abbreviations(Path('text_info'), generate_sorted_abbreviations=False)

class Agronomist(BaseModel):
    fullName: str
    telegramId: str

class AgroMessage(BaseModel):
    agronomist: Agronomist
    report: Optional[str] = None
    photoUrl: Optional[str] = None

class MessageClassification(BaseModel):
    date: datetime
    department: str
    operation: str
    plant: str
    perDay: int
    perOperation: int
    grosPerDay: int
    grosPerOperation: int

class ImageURL(BaseModel):
    url: str

def parse_int(str_value: str, default = -1):
    try:
        return int(str_value)
    except (ValueError, TypeError):
        logging.error(f"Failed to parse str value {str_value} to int")
        return default

@app.post("/messages/proc_many")
async def process_messages(messages: list[AgroMessage]):
    
    text_reports = [m.report for m in messages if m.report]
    only_photo_report_urls = [m.photoUrl for m in messages if not m.report and m.photoUrl]
    
    print(f"Text reports: { text_reports }")
    print(f"Photo url reports: { only_photo_report_urls }")

    responses = []
    if len(text_reports) > 0:

        classified_messages = classify(text_reports, abbreviations)
        print(classified_messages)

        for message in classified_messages:
            for field in message:
                
                department = ''
                if 'department' in field:
                    department = 'АОР' if 'отд' in field['department'] else field['department']
                
                classified_message = MessageClassification(
                    date = datetime.now(),
                    department = department,
                    operation = field.get('operation', ''),
                    plant = field.get('plant', ''),
                    perDay = parse_int(field.get('perDay')),
                    perOperation = parse_int(field.get('perOperation')),
                    grosPerDay= parse_int(field.get('grosPerDay')),
                    grosPerOperation= parse_int(field.get('grosPerOperation'))
                )
                responses.append(classified_message)
    
    # Обработка URL изображений, если они есть
    if len(only_photo_report_urls) > 0:
        for photo_url in only_photo_report_urls:
            try:
                image_data = ImageURL(url=photo_url)
                result = await analyze_image_by_url(image_data)
                # Здесь можно добавить обработку результата и добавление в responses
            except Exception as e:
                logging.error(f"Ошибка при обработке фото по URL {photo_url}: {str(e)}")
                
    return {"response": responses}



@app.post("/analyze_llm_by_url")
async def analyze_image_by_url(image_data: ImageURL):
    """
    Эндпоинт для анализа изображения по URL с помощью LLaVA модели.
    Принимает URL изображения, скачивает его и обрабатывает.
    """
    try:
        logging.info(f"Получен запрос на анализ изображения по URL: {image_data.url}")
        
        # Создаем временную директорию для файлов
        with tempfile.TemporaryDirectory() as temp_dir:
            # Скачиваем изображение по URL
            try:
                response = requests.get(image_data.url, stream=True, timeout=30)
                response.raise_for_status()  # Проверяем успешность запроса
                
                # Определяем имя файла из URL
                image_filename = os.path.basename(image_data.url.split('?')[0])
                if not image_filename:
                    image_filename = "downloaded_image.jpg"
                
                # Сохраняем загруженное изображение
                image_path = os.path.join(temp_dir, image_filename)
                with open(image_path, "wb") as f:
                    for chunk in response.iter_content(chunk_size=8192):
                        f.write(chunk)
                
                logging.info(f"Изображение успешно скачано и сохранено: {image_path}")
            except Exception as e:
                error_msg = f"Ошибка при скачивании изображения: {str(e)}"
                logging.error(error_msg)
                return JSONResponse(
                    status_code=500,
                    content={"error": error_msg}
                )
            
            # Запускаем анализ
            result = run_llava(image_path)
            
            return {"response": result}
            
    except Exception as e:
        error_msg = f"Произошла ошибка при обработке запроса: {str(e)}"
        logging.error(error_msg)
        return JSONResponse(
            status_code=500,
            content={"error": error_msg}
        )



@app.post("/analyze_table_by_url")
async def analyze_table_by_url(image_data: ImageURL):
    """
    Эндпоинт для анализа таблицы по URL:
    1. Скачивает изображение по URL
    2. Обрабатывает изображение таблицы
    3. Отправляет обработанное изображение в модель
    4. Возвращает JSON результат
    """
    try:
        logging.info(f"Получен запрос на анализ таблицы по URL: {image_data.url}")
        
        # Создаем временную директорию для файлов
        with tempfile.TemporaryDirectory() as temp_dir:
            # Скачиваем изображение по URL
            try:
                response = requests.get(image_data.url, stream=True, timeout=30)
                response.raise_for_status()  # Проверяем успешность запроса
                
                # Определяем имя файла из URL
                image_filename = os.path.basename(image_data.url.split('?')[0])
                if not image_filename:
                    image_filename = "downloaded_image.jpg"
                
                # Сохраняем исходное изображение
                original_path = os.path.join(temp_dir, "original_" + image_filename)
                with open(original_path, "wb") as f:
                    for chunk in response.iter_content(chunk_size=8192):
                        f.write(chunk)
                
                logging.info(f"Изображение успешно скачано и сохранено: {original_path}")
            except Exception as e:
                error_msg = f"Ошибка при скачивании изображения: {str(e)}"
                logging.error(error_msg)
                return JSONResponse(
                    status_code=500,
                    content={"error": error_msg}
                )
            
            # Путь для обработанного изображения
            processed_path = os.path.join(temp_dir, "processed_" + image_filename)
            
            # Обрабатываем таблицу
            try:
                process_table_image(original_path, processed_path)
                logging.info(f"Таблица успешно обработана: {processed_path}")
            except Exception as e:
                error_msg = f"Ошибка при обработке таблицы: {str(e)}"
                logging.error(error_msg)
                return JSONResponse(
                    status_code=500,
                    content={"error": error_msg}
                )
            
            # Отправляем обработанное изображение в модель
            result = run_llava(processed_path)
            
            return {"response": result}
            
    except Exception as e:
        error_msg = f"Произошла ошибка при обработке запроса: {str(e)}"
        logging.error(error_msg)
        return JSONResponse(
            status_code=500,
            content={"error": error_msg}
        )

@app.get("/health")
async def health_check():
    """Проверка работоспособности сервиса"""
    return {
        "status": "healthy",
        "server": "running"
    }
