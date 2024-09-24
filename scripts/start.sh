#!/bin/bash

PROJECT_ROOT="/home/ec2-user/app"
JAR_FILE="$PROJECT_ROOT/cep-0.0.2.jar"

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

# build 파일 복사
echo "$TIME_NOW > $JAR_FILE 파일 복사" >> $DEPLOY_LOG
if cp $PROJECT_ROOT/build/libs/*.jar $JAR_FILE; then
    echo "$TIME_NOW > $JAR_FILE 파일 복사 성공" >> $DEPLOY_LOG
else
    echo "$TIME_NOW > $JAR_FILE 파일 복사 실패" >> $DEPLOY_LOG
    exit 1  # 스크립트를 종료
fi

# jar 파일 실행
echo "$TIME_NOW > $JAR_FILE 파일 실행" >> $DEPLOY_LOG
nohup java -jar $JAR_FILE > $APP_LOG 2> $ERROR_LOG &

sleep 2  # JAR 파일이 시작되는 시간을 기다리기
CURRENT_PID=$(pgrep -f $JAR_FILE)

if [ -n "$CURRENT_PID" ]; then
    echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> $DEPLOY_LOG
else
    echo "$TIME_NOW > JAR 파일 실행 실패" >> $DEPLOY_LOG
    exit 1  # 스크립트를 종료
fi
