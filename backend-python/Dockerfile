FROM  pytorch/pytorch:2.1.0-cuda12.1-cudnn8-runtime

WORKDIR /app

COPY . .

RUN pip install -r requirements.txt

CMD [ "uvicorn","main:app","--host","0.0.0.0","--port","1607"]


