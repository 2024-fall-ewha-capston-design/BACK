from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from intoGPT import analyze_chat
from typing import List, Dict
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

# ✅ 예외 핸들러 추가: FastAPI에서 422 오류 이유를 출력
from fastapi import FastAPI, Request

app = FastAPI()

@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    return JSONResponse(
        status_code=422,
        content={"detail": exc.errors()},
    )

class ChatAnalysisRequest(BaseModel):
    messages: List[str]
    participant_keywords: Dict[str, List[str]]  # JSON은 Key를 String으로 처리하므로 수정 필요


@app.post("/analyze_chat")
async def analyze_chat_endpoint(request: Request):
    import json
    from fastapi.responses import JSONResponse

    try:
        body = await request.json()  # ✅ 원본 JSON 데이터 확인
        print("📩 FastAPI가 받은 JSON 데이터:", json.dumps(body, indent=4, ensure_ascii=False))

        # 🚀 JSON 키 변환: participantKeywords → participant_keywords
        if "participantKeywords" in body:
            body["participant_keywords"] = body.pop("participantKeywords")

        # ✅ 요청 데이터 Pydantic 모델 변환
        chat_request = ChatAnalysisRequest(**body)

        # ✅ Dict Key를 String으로 변환
        participant_keywords = {str(k): v for k, v in chat_request.participant_keywords.items()}

        # 🔥 채팅 분석 실행
        result = analyze_chat(chat_request.messages, participant_keywords)

        # ✅ 항상 JSON 배열을 반환하도록 보장
        if not isinstance(result, list):
            print("⚠️ 예상과 다른 데이터 타입 감지 → 빈 리스트 반환")
            result = []

        # ✅ 최종 응답 데이터를 Dict Key를 String으로 변환
        result = [{str(k): v for k, v in item.items()} for item in result]

        print(f"✅ FastAPI 분석 결과 반환: {result}")

        # 🔹 JSON 배열 보장
        return JSONResponse(content=result)

    except Exception as e:
        print("🔥 FastAPI 500 에러 발생:")
        return JSONResponse(status_code=500, content=[])
