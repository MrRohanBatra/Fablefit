import re
from typing import Optional
import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

MEN_WORDS   = {"men", "man", "mens", "male", "guy"}
WOMEN_WORDS = {"women", "woman", "ladies", "female", "girl"}
KIDS_WORDS  = {"kids", "child", "children", "kid"}

SYNONYMS = {
    "men":   ["man", "mens", "male"],
    "women": ["woman", "ladies", "female"],
    "kids":  ["child", "children", "kid"],
}


class Utils:

    def serializeDoc(self, doc: dict) -> dict:
        doc["_id"] = str(doc["_id"])
        return doc

    # ----------------------------
    # 1️⃣ Gender Detection
    # ----------------------------
    def detect_gender(self, query: str) -> Optional[str]:
        words = set(query.lower().split())

        if words & MEN_WORDS:
            return "Men"
        if words & WOMEN_WORDS:
            return "Women"
        if words & KIDS_WORDS:
            return "Kids"

        return None

    # ----------------------------
    # 2️⃣ Query Expansion
    # ----------------------------
    def expand_query(self, query: str) -> str:
        words = query.lower().split()
        expanded = list(dict.fromkeys(words))

        for word in words:
            for synonym in SYNONYMS.get(word, []):
                if synonym not in expanded:
                    expanded.append(synonym)

        return " ".join(expanded)

    # ----------------------------
    # 3️⃣ Gender Match Check
    # ----------------------------
    def matches_gender(self, product: dict, gender: str) -> bool:
        gender_lower = gender.lower()

        return (
            gender_lower in (product.get("category") or "").lower()
            or gender_lower in (product.get("description") or "").lower()
            or gender_lower in (product.get("name") or "").lower()
        )

    # ----------------------------
    # 4️⃣ Fallback Text Search
    # ----------------------------
    def fallback_search(
        self,
        products: list[dict],
        expanded_query: str,
        gender: Optional[str]
    ) -> list[dict]:

        q_words = [w.strip() for w in expanded_query.split() if w.strip()]
        matched = []

        for p in products:
            text = f"{p.get('name','')} {p.get('description','')} {p.get('category','')}".lower()

            if any(re.search(rf"\b{re.escape(w)}\b", text, re.IGNORECASE) for w in q_words):
                matched.append(p)

        if gender:
            matched = [p for p in matched if self.matches_gender(p, gender)]

        return matched