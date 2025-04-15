from fastapi import FastAPI
from pydantic import BaseModel
from datetime import datetime
from pathlib import Path
from main import process_texts, generate_abbreviations

abbreviations = generate_abbreviations(Path('./text_info'), generate_sorted_abbreviations=False)

app = FastAPI()

class Agronomist(BaseModel):
    fullName: str
    telegramId: str

class AgroMessage(BaseModel):
    agronomist: Agronomist
    text: str

class MessageClassification(BaseModel):
    data: datetime
    department: str
    operation: str
    plant: str
    perDay: int
    perOperation: int
    grosPerDay: int
    grosPerOperation: int

def parse_int(string):
    try:
        return int(string)
    except ValueError:
        return 0

@app.post("/messages/proc_many")
async def process_messages(messages: list[AgroMessage]):
    texts = [m.text for m in messages]
    outputs = process_texts(texts, abbreviations)

    responses = []
    for output in outputs:
        for dict in output:
            department = ''
            if 'department' in dict:
                department = 'АОР' if 'отд' in dict['department'] else dict['department']

            responses.append(MessageClassification(
                data = dict.get('data', datetime.now()),
                department = department,
                operation = dict.get('operation', ''),
                plant = dict.get('plant', ''),
                perDay = parse_int(dict.get('perDay', '')),
                perOperation = parse_int(dict.get('perOperation', '')),
                grosPerDay = parse_int(dict.get('grosPerDay', '')),
                grosPerOperation = parse_int(dict.get('grosPerOperation', '')),
            ))
    
    return {"response": responses}
