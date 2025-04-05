import os
import json
from dotenv import load_dotenv
#rom langchain.chat_models import ChatOpenAI
from langchain_community.chat_models import ChatOpenAI
from langchain.prompts import PromptTemplate
from langchain.chains import LLMChain

load_dotenv()

# OpenAI 설정
openai_api_key = os.getenv("OPENAI_API_KEY")
os.environ["OPENAI_API_KEY"] = openai_api_key
chat_model = ChatOpenAI(model="gpt-3.5-turbo-1106", temperature=0)

# 프롬프트 템플릿
template = """
You are an AI assistant analyzing chat messages for specific keywords.

Chat history:
{chat_history}

Participant keywords:
{participant_keywords}

For each message, identify if it contains any of the participant's keywords and return the result in JSON format:
- chat_id
- keyword_id
- participant_id

If no keywords match, return an empty list [].
"""

prompt = PromptTemplate(
    input_variables=["chat_history", "participant_keywords"],
    template=template,
)

chain = LLMChain(llm=chat_model, prompt=prompt)

def analyze_chat(chat_history: list, participant_keywords: dict):
    try:
        # ✅ LangChain의 LLMChain을 실행하여 분석
        response = chain.run(
            chat_history=json.dumps(chat_history, ensure_ascii=False),
            participant_keywords=json.dumps(participant_keywords, ensure_ascii=False)
        )

        # ✅ LLM 응답을 JSON으로 변환
        results = json.loads(response)

        print(f"✅ FastAPI 분석 결과 반환: {json.dumps(results, indent=4, ensure_ascii=False)}")
        return results

    except json.JSONDecodeError:
        print("🔥 LLM 응답을 JSON으로 변환하는 데 실패했습니다.")
        return []
    #try:
    #    response = chain.run(chat_history=json.dumps(chat_history), participant_keywords=json.dumps(participant_keywords))
     #   return json.loads(response)
    #except json.JSONDecodeError:
     #   return {"error": "Invalid JSON response from the model"}






