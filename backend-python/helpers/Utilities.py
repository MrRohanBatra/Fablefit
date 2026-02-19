class Utils():
    def __init__(self):
        pass
    def serializeDoc(self,doc):
        doc["_id"]=str(doc["_id"])
        return doc