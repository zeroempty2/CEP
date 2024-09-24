#!/bin/bash

APP_NAME="cep-0.0.2.jar"
PROJECT_ROOT="/home/ec2-user/app"

DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

# 현재 실행 중인 프로세스의 PID 탐색
PID=$(pgrep -f $APP_NAME)

# PID가 존재하는 경우, 프로세스를 종료.
if [ -n "$PID" ]; then
  echo "Stopping application with PID: $PID"
  kill -9 $PID
  echo "Application stopped successfully."
else
  echo "No application is currently running."
fi
