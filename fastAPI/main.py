from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from intoGPT import filter_chat

app = FastAPI()

# 요주의 인물 뽑아내기 위한 입력 데이터
class ChatFilterRequest(BaseModel):
    chat_history: str
    negative_keywords: str

@app.post("/filter_negative_chat")
async def filter_chat_endpoint(request: ChatFilterRequest):
    # inputGPT 모듈 호출
    try:
        response = filter_chat(
            chat_history=request.chat_history,
            negative_keywords=request.negative_keywords,
        #print(text)
        )
        return {"filtered_messages": response}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

    #return request.negative_keywords

# fast api 연결 테스트
@app.get("/")
async def root():
    return {"message": "Hello World"}