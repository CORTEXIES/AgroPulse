import logging
import os
import tempfile
from datetime import datetime
from pathlib import Path
from typing import Optional

import requests
from fastapi import FastAPI
from fastapi.responses import JSONResponse
from main import classify, generate_abbreviations
from pydantic import BaseModel
from scripts.model.model import load_pretrained_model
from scripts.model_photo.app import process_table_image, run_llava

app = FastAPI()
abbreviations = generate_abbreviations(Path('text_info'), generate_sorted_abbreviations=False)
tokenizer, model = load_pretrained_model()

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
    
    devided_reports = []
    for report in text_reports:
        devided_reports += list(report.split('\n\n'))

    responses = []
    if len(text_reports) > 0:
        classified_messages = classify(devided_reports, abbreviations, tokenizer, model)
        print(classified_messages)

        for message in classified_messages:
            for field in message:
                
                department = 'АОР'
                if 'department' in field and field['department'] != 'по': # "по" идентифицирует 'АОР'
                    predicted_dep = field['department']
                    aor_list = ['кавказ', "север", 'центр', 'юг', 'Рассвет']
                    if all(item not in predicted_dep for item in aor_list):
                        department = predicted_dep

                date = datetime.now()
                if 'data' in field and isinstance(field['data'], str):
                    date_str = field['data'].strip()
                
                    date_parts = list(filter(lambda x: x.strip(), date_str.split('.')))
                
                    try:
                        day = int(date_parts[0])
                        month = int(date_parts[1])
                        year = int(date_parts[2]) if len(date_parts) > 2 else datetime.now().year
                        
                        date = datetime(day=day, month=month, year=year)
                    except (ValueError, IndexError):
                        pass


                classified_message = MessageClassification(
                    date = date,
                    department = department,
                    operation = field.get('operation', ''),
                    plant = field.get('plant', ''),
                    perDay = parse_int(field.get('perDay')),
                    perOperation = parse_int(field.get('perOperation')),
                    grosPerDay= parse_int(field.get('grosPerDay')),
                    grosPerOperation= parse_int(field.get('grosPerOperation'))
                )
                responses.append(classified_message)
    
    if len(only_photo_report_urls) > 0:
        for photo_url in only_photo_report_urls:
            try:
                image_data = ImageURL(url=photo_url)
                result = await analyze_image_by_url(image_data)
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
        
        with tempfile.TemporaryDirectory() as temp_dir:
            try:
                response = requests.get(image_data.url, stream=True, timeout=30)
                response.raise_for_status()
                
                image_filename = os.path.basename(image_data.url.split('?')[0])
                if not image_filename:
                    image_filename = "downloaded_image.jpg"
                
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
        
        with tempfile.TemporaryDirectory() as temp_dir:
            try:
                response = requests.get(image_data.url, stream=True, timeout=30)
                response.raise_for_status()  # Проверяем успешность запроса
                
                image_filename = os.path.basename(image_data.url.split('?')[0])
                if not image_filename:
                    image_filename = "downloaded_image.jpg"
                
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
            
            processed_path = os.path.join(temp_dir, "processed_" + image_filename)
            
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
