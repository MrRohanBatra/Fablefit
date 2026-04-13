from langchain_community.chat_models import ChatOllama
from langchain.prompts import ChatPromptTemplate
import json

llm = ChatOllama(model="llama3", temperature=0)

prompt = ChatPromptTemplate.from_template("""
Extract intent from user message.

Return ONLY JSON:

{{
  "intent": "search/select/confirm/show_orders/unknown",
  "query": "",
  "product_name": "",
  "confirmation": "yes/no"
}}

Message: {message}
""")

async def extract_intent(message: str):
    chain = prompt | llm
    response = await chain.ainvoke({"message": message})

    try:
        return json.loads(response.content)
    except:
        return {
            "intent": "unknown",
            "query": message,
            "product_name": "",
            "confirmation": "no"
        }