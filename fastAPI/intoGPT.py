# import os
# import re

import os
import json
from dotenv import load_dotenv
from langchain.chat_models import ChatOpenAI
from langchain.prompts import PromptTemplate
from langchain.chains import LLMChain

load_dotenv()

# 환경 변수에서 OpenAI API 키 가져오기
openai_api_key = os.getenv("OPENAI_API_KEY")

os.environ["OPENAI_API_KEY"] = openai_api_key
model = "gpt-3.5-turbo"

chat_model = ChatOpenAI(model="gpt-3.5-turbo", temperature=0.7)


# 요주 인물 처리를 위한 프롬프트 템플릿 정의
template = """
You are an AI assistant helping to filter messages in a chat conversation.

Here is the chat history:
{chat_history}

The following keywords represent unwanted content:
{negative_keywords}

Identify all messages that contain any of the unwanted content and provide the following information in JSON format:
- The message content
- The username of the sender

If there are no unwanted messages, respond with an empty list [].
"""

prompt = PromptTemplate(
    input_variables=["chat_history", "negative_keywords"],
    template=template,
)

# LangChain LLMChain 연결
chain = LLMChain(llm=chat_model, prompt=prompt)

# 메시지 필터링 함수
def filter_chat(chat_history: str, negative_keywords: str):
    try:
        # LangChain 호출
        response = chain.run(chat_history=chat_history, negative_keywords=negative_keywords)
        # JSON 변환
        json_response = json.loads(response)
        return json_response
    except json.JSONDecodeError:
        return {"error": "Invalid JSON response from the model"}





