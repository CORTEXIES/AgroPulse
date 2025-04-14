from fastapi import FastAPI
from pydantic import BaseModel
from datetime import datetime
from pathlib import Path
from main import process_texts, generate_abbreviations
# import asyncio

abbreviations = generate_abbreviations(Path('./text_info'), generate_sorted_abbreviations=False)

app = FastAPI()

class AgroMessage(BaseModel):
    senderName: str
    telegramId: str
    text: str

class MessageClassification(BaseModel):
    data: str
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
                data = dict.get('data', str(datetime.now())),
                department = department,
                operation = dict.get('operation', ''),
                plant = dict.get('plant', ''),
                perDay = parse_int(dict.get('perDay', '')),
                perOperation = parse_int(dict.get('perOperation', '')),
                grosPerDay = parse_int(dict.get('grosPerDay', '')),
                grosPerOperation = parse_int(dict.get('grosPerOperation', '')),
            ))
    
    
    return {"response": responses}

# input = [
#     AgroMessage(
#         senderName="Danil Chist",
#         telegramId="5040126939",
#         text="Пахота зяби под мн тр\nПо Пу 26/488\nОтд 12 26/221\n\nПредп культ под оз пш\nПо Пу 215/1015\nОтд 12 128/317\nОтд 16 123/529\n\n2-е диск сах св под пш\nПо Пу 22/627\nОтд 11 22/217\n\n2-е диск сои под оз пш\nПо Пу 45/1907\nОтд 12 45/299"
#     ),
#     AgroMessage(
#         senderName="Danil Chist",
#         telegramId="5040126939",
#         text="ТСК \nВыравнивание зяби под сою 199 га/ с нарастающим 533 га (8%) Остаток 5564 га\nОсадки 2 мм",
#     )
# ]

# output = asyncio.run(process_messages(input))

# print(output)
