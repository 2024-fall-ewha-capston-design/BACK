#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu/happyCoder"
JAR_FILE="$PROJECT_ROOT/cockChat-webapp.jar"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"
TIME_NOW=$(date +%c)

# ✅ Spring Boot 종료
CURRENT_PID=$(pgrep -f $JAR_FILE)
if [ -n "$CURRENT_PID" ]; then
  kill -15 "$CURRENT_PID"
  sleep 5
fi

# ✅ FastAPI 종료
FASTAPI_PID=$(pgrep -f "uvicorn main:app")
if [ -n "$FASTAPI_PID" ]; then
  kill -15 "$FASTAPI_PID"
  sleep 3
fi

