from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

class AgroMessage(BaseModel):
    content: str
    
@app.post("/messages/proc_one")
async def process_message(message: AgroMessage):
    return {"response": f"Полученное сообщение: {message.content}"}

@app.post("/messages/proc_many")
async def process_messages(messages: list[AgroMessage]):
    for i in range(len(messages)):
        messages[i].content = f"Полученное сообщение: {messages[i].content}"
    
    return {"response": messages}
