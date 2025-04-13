import os
import json
from dotenv import load_dotenv
from typing import List, Dict
from langchain.prompts import ChatPromptTemplate
from langchain.chat_models import ChatOpenAI
from langchain_core.output_parsers import JsonOutputParser

# 🔹 환경 변수 로드
load_dotenv()
openai_api_key = os.getenv("OPENAI_API_KEY")
os.environ["OPENAI_API_KEY"] = openai_api_key

# 🔹 LLM 세팅
chat_model = ChatOpenAI(model="gpt-4-0125-preview", temperature=0)

# 🔹 JSON 파서 (LLM 응답을 JSON으로 파싱)
parser = JsonOutputParser()

# 🔹 프롬프트 템플릿 구성
prompt = ChatPromptTemplate.from_messages([
    ("system",
     "너는 사용자 메시지를 participant의 키워드와 비교해서 의미적으로 일치하는 항목을 찾아 JSON으로 반환하는 역할을 해.\n"
     "다음은 그 규칙이야:\n"
     "- messages는 채팅 메시지들의 리스트야. 각 항목은 문자열이야.\n"
     "- participant_keywords는 참가자별로 등록한 키워드 리스트야. 각 키워드는 keyword_id와 content를 가지고 있어.\n"
     "- 너는 각 메시지가 어떤 참가자의 어떤 키워드와 의미적으로 일치하는지 판단해서, 다음과 같은 형식의 JSON 리스트로 반환해줘:\n"
     "[\n"
     "  {{\n"
     "    \"chat_id\": \"<메시지 본문에서 제공된 실제 ID>\",\n"
     "    \"participant_id\": \"<참가자 ID>\",\n"
     "    \"keyword_id\": \"<해당 키워드 ID>\"\n"
     "  }}\n"
     "]\n"
     "- chat_id는 메시지에서 제공된 실제 ID (예: 67f735619c77125f88166b2e)를 그대로 사용해줘.\n"
     "- 반드시 JSON 형식에 맞게 출력해. 리스트가 비어도 [] 형태로 반환해.\n"
     "- 메시지 '안녕'은 인사말이므로 '인사'라는 키워드와 일치해야 해. 예를 들어, '안녕하세요', '하이', '여보세요'와 같은 인사말도 '인사' 키워드와 일치해야 해.\n"
     "- 만약 메시지가 참가자의 키워드와 의미적으로 비슷한 경우, 그 메시지를 해당 키워드와 일치한다고 판단해. 예를 들어, 'Hello'는 'greeting' 키워드와 일치한다고 판단할 수 있어."),

    ("human",
     "messages: {messages}\n\nparticipant_keywords: {participant_keywords}")
])

# 🔹 LangChain chain 구성
chain = prompt | chat_model | parser

# 🔹 분석 함수
def perform_analysis(messages: List[str], participant_keywords: Dict[str, List[Dict[str, str]]]) -> List[Dict[str, str]]:
    try:
        # GPT로 분석 요청
        result = chain.invoke({
            "messages": messages,
            "participant_keywords": participant_keywords
        })
        return result
    except Exception as e:
        print("🚨 GPT 처리 중 오류 발생:", str(e))
        return []
