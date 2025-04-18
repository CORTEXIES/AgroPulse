import logging
from fastapi import FastAPI
from pydantic import BaseModel
from datetime import datetime
from pathlib import Path
from main import classify, generate_abbreviations
from scripts.model.model import load_pretrained_model

app = FastAPI()
abbreviations = generate_abbreviations(Path('./text_info'), generate_sorted_abbreviations=False)
tokenizer, model = load_pretrained_model()

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
        # logging.error(f"Failed to parse str value {str_value} to int")
        return default

@app.post("/messages/proc_many")
async def process_messages(messages: list[AgroMessage]):
    
    reports = [m.report for m in messages]
    devided_reports = []
    for report in reports:
        devided_reports += list(report.split('\n\n'))
    # print(devided_reports)
    classified_messages = classify(devided_reports, abbreviations, tokenizer, model)
    # print(classified_messages)

    responses = []
    for message in classified_messages:
        for dict in message:
            
            # Department check
            department = 'АОР'
            if 'department' in dict and dict['department'] != 'по': # "по" идентифицирует 'АОР'
                predicted_dep = dict['department']
                aor_list = ['кавказ', "север", 'центр', 'юг', 'Рассвет']
                if all(item not in predicted_dep for item in aor_list):
                    department = predicted_dep

            # Date check
            date = datetime.now()
            if 'data' in dict and isinstance(dict['data'], str):
                date_str = dict['data'].strip()
                
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
                operation = dict.get('operation', ''),
                plant = dict.get('plant', ''),
                perDay = parse_int(dict.get('perDay')),
                perOperation = parse_int(dict.get('perOperation')),
                grosPerDay= parse_int(dict.get('grosPerDay')),
                grosPerOperation= parse_int(dict.get('grosPerOperation'))
            )
            responses.append(classified_message)
        
    # print(responses)
    return {"response": responses}
