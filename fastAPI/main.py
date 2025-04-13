from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError
from pydantic import BaseModel, Field
from typing import List, Dict
import json

from intoGPT import perform_analysis  # 너의 GPT 호출 함수

app = FastAPI()

@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    return JSONResponse(
        status_code=422,
        content={"detail": exc.errors()},
    )


class Keyword(BaseModel):
    keyword_id: str
    content: str

class ChatRequest(BaseModel):
    messages: List[str]
    participant_keywords: Dict[str, List[Keyword]]




# 🔹 응답 예시 (chat_id, participant_id, keyword_id 조합 리스트)
@app.post("/analyze_chat")
async def analyze_chat(request: ChatRequest):
    try:
        print("✅ Parsed request:", request)

        # participant_keywords 변환
        keywords_dict = {
            pid: [k.dict() for k in kws] for pid, kws in request.participant_keywords.items()
        }
        print("🔵 변환된 participant_keywords:", keywords_dict)

        result = perform_analysis(request.messages, keywords_dict)
        print("🟣 분석 결과:", result)

        #return {"result": result}
        return result

    except Exception as e:
        print("❌ 예외 발생:", str(e))
        return {"error": str(e)}
