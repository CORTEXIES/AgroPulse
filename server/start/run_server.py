import logging
from fastapi import FastAPI
from pydantic import BaseModel
from datetime import datetime
from pathlib import Path
from main import classify, generate_abbreviations
from typing import Optional

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
        
    return {"response": responses}
