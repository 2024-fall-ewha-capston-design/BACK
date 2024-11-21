from fastapi import FastAPI

app = FastAPI()

# fast api 연결 테스트
@app.get("/")
async def root():
    return {"message": "Hello World"}