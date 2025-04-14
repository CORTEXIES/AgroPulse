from typing import override
from fastapi import FastAPI
from pydantic import BaseModel
import random
from datetime import datetime

app = FastAPI()

class AgroMessage(BaseModel):
    senderName: str
    telegramId: str
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


@app.post("/messages/proc_many")
async def process_messages(messages: list[AgroMessage]):
    responses = []
    for i in range(len(messages)):
        m = messages[i]
        response = MessageClassification(
            data=datetime.now(),
            department=f"Имя пользователя: {m.senderName}",
            operation=f"Telegram id: {m.telegramId}",
            plant=f"SOME PLANT",
            perDay=random.randint(1, 100000),
            perOperation=random.randint(1, 100000),
            grosPerDay=random.randint(1, 100000),
            grosPerOperation=random.randint(1, 100000),
        )
        responses.append(response)
    
    return {"response": responses}
