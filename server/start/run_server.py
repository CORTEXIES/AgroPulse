import logging
from fastapi import FastAPI
from pydantic import BaseModel
from datetime import datetime
from pathlib import Path
from main import classify, generate_abbreviations

app = FastAPI()
abbreviations = generate_abbreviations(Path('./start/text_info'), generate_sorted_abbreviations=False)

class Agronomist(BaseModel):
    fullName: str
    telegramId: str

class AgroMessage(BaseModel):
    agronomist: Agronomist
    report: str

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
    
    reports = [m.report for m in messages]
    classified_messages = classify(reports, abbreviations)
    print(classified_messages)

    responses = []
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
        
    print(responses)
    return {"response": responses}
