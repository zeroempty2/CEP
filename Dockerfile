FROM ubuntu:22.04

# 빌드 아규먼트 설정
ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_ACCESS_KEY

# 환경 변수 설정
ENV AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
ENV AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}

ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Asia/Seoul

# 필수 의존성 설치
RUN apt-get update && apt-get install -y --no-install-recommends \
    libappindicator3-1 \
    libxss1 \
    libasound2 \
    unzip \
    wget \
    curl \
    apt-transport-https \
    ca-certificates \
    gnupg \
    awscli \
    openjdk-17-jdk && \
    ln -fs /usr/share/zoneinfo/$TZ /etc/localtime && \
    dpkg-reconfigure --frontend noninteractive tzdata

# AWS 자격증명 설정
RUN mkdir -p ~/.aws && \
    echo "[default]" > ~/.aws/credentials && \
    echo "aws_access_key_id=${AWS_ACCESS_KEY_ID}" >> ~/.aws/credentials && \
    echo "aws_secret_access_key=${AWS_SECRET_ACCESS_KEY}" >> ~/.aws/credentials

# S3에서 설치 파일 다운로드
RUN aws s3 cp s3://s3cepcdbucket/google-chrome.deb /tmp/google-chrome.deb && \
    aws s3 cp s3://s3cepcdbucket/chromedriver-linux64.zip /tmp/chromedriver-linux64.zip

# Chrome 설치
RUN dpkg -i /tmp/google-chrome.deb || apt-get install -y -f && \
    rm /tmp/google-chrome.deb

# chromedriver 설치
RUN unzip /tmp/chromedriver-linux64.zip -d /usr/local/bin/ && \
    chmod +x /usr/local/bin/chromedriver-linux64/chromedriver && \
    rm /tmp/chromedriver-linux64.zip

# chromedriver의 경로를 PATH에 추가
ENV PATH="/usr/local/bin/chromedriver-linux64:${PATH}"

# 크롬과 크롬 드라이버 버전 확인 (디버깅용)
RUN google-chrome --version
RUN chromedriver --version

# 기본 작업 디렉토리 설정 (필요에 따라 설정 가능)
WORKDIR /scheduler

# 빌드 단계에서 생성된 JAR 파일 복사
COPY build/libs/*.jar scheduler.jar

# 설치 후 정리 (캐시와 임시 파일 삭제)
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "scheduler.jar"]
