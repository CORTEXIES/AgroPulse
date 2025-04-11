from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

class AgroMessage(BaseModel):
    content: str
    
@app.post("/api/v1/neuro")
async def process_message(message: AgroMessage):
    return {"message": f"Полученное сообщение: {message.content}"}
