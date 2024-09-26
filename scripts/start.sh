##!/bin/bash
#
#PROJECT_ROOT="/home/ec2-user/app"
#DEPLOY_LOG="$PROJECT_ROOT/deploy.log"
#TIME_NOW=$(date +%c)
#
#DOCKER_IMAGE_NAME="2zeroempty/cepdocker:latest"
#CONTAINER_NAME="cep-app"
#
## 환경변수 참조
#source /home/ec2-user/.bashrc
#
## 기존 컨테이너가 있으면 삭제
#EXISTING_CONTAINER=$(docker ps -q -f name=$CONTAINER_NAME)
#
#if [ -n "$EXISTING_CONTAINER" ]; then
#  echo "$TIME_NOW > 기존 컨테이너 중지 및 삭제" >> $DEPLOY_LOG
#  docker stop $CONTAINER_NAME
#  docker rm $CONTAINER_NAME
#fi
#
## Docker 이미지 실행
#echo "$TIME_NOW > Docker 컨테이너 실행" >> $DEPLOY_LOG
#docker run -d -p 8080:8080 --name $CONTAINER_NAME $DOCKER_IMAGE_NAME
#
## 컨테이너가 정상적으로 실행되었는지 확인
#if [ "$(docker ps -q -f name=$CONTAINER_NAME)" ]; then
#    echo "$TIME_NOW > Docker 컨테이너가 성공적으로 실행되었습니다." >> $DEPLOY_LOG
#else
#    echo "$TIME_NOW > Docker 컨테이너 실행 실패" >> $DEPLOY_LOG
#    exit 1
#fi
