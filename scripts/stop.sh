##!/bin/bash
#
#CONTAINER_NAME="cep-app"
#DEPLOY_LOG="/home/ec2-user/app/deploy.log"
#TIME_NOW=$(date +%c)
#
## 실행 중인 컨테이너 확인 및 종료
#EXISTING_CONTAINER=$(docker ps -q -f name=$CONTAINER_NAME)
#
#if [ -n "$EXISTING_CONTAINER" ]; then
#  echo "$TIME_NOW > 실행 중인 컨테이너 $CONTAINER_NAME 중지" >> $DEPLOY_LOG
#  docker stop $CONTAINER_NAME
#  docker rm $CONTAINER_NAME
#  echo "$TIME_NOW > 컨테이너 중지 및 삭제 완료" >> $DEPLOY_LOG
#else
#  echo "$TIME_NOW > 실행 중인 컨테이너가 없습니다." >> $DEPLOY_LOG
#fi
