#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu/happyCoder"
JAR_FILE="$PROJECT_ROOT/cockChat-webapp.jar"
FASTAPI_DIR="$PROJECT_ROOT/fastAPI"
FASTAPI_LOG="$PROJECT_ROOT/fastapi.log"

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

echo "$TIME_NOW > 배포 시작" >> $DEPLOY_LOG

# ✅ Spring Boot 실행
cp $PROJECT_ROOT/build/libs/*.jar $JAR_FILE
nohup java -jar $JAR_FILE > $APP_LOG 2> $ERROR_LOG &

# ✅ FastAPI 실행
cd $FASTAPI_DIR

python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt

FASTAPI_PID=$(pgrep -f "uvicorn main:app")
if [ -n "$FASTAPI_PID" ]; then
  kill -15 "$FASTAPI_PID"
  sleep 3
fi

#nohup uvicorn main:app --host 0.0.0.0 --port 8000 > $FASTAPI_LOG 2>&1 &
nohup ./venv/bin/uvicorn main:app --host 0.0.0.0 --port 8000 > $FASTAPI_LOG 2>&1 &
