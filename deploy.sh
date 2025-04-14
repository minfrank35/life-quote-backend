#!/bin/bash

# 변수 정의
JAR_NAME=dailyharu-0.0.1-SNAPSHOT.jar
LOCAL_JAR_PATH=D:/cursorProjects/dailyharu2/target
REMOTE_USER=ec2-user
REMOTE_HOST=dailyharu.duckdns.org
REMOTE_DIR=/home/ec2-user
PEM_PATH="/C/Users/jskim/Desktop/luna-key.pem"
REMOTE_JAR_PATH=$REMOTE_DIR/$JAR_NAME
PORT=8080

echo "🔨 [1/4] Maven 빌드 (테스트 제외)..."
./mvnw.cmd clean package -DskipTests

echo "📦 [2/4] JAR 파일 EC2로 업로드 중..."
scp -i "$PEM_PATH" "$LOCAL_JAR_PATH/$JAR_NAME" $REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR/

echo "🛠️  [3/4] 원격 서버에서 실행 중인 프로세스 종료 및 재시작..."
ssh -i "$PEM_PATH" $REMOTE_USER@$REMOTE_HOST <<EOF
  echo "🔍 포트 $PORT 확인 중..."
  PID=\$(lsof -ti tcp:$PORT)
  if [ ! -z "\$PID" ]; then
    echo "🛑 포트 $PORT 사용 중인 프로세스(\$PID) 종료 중..."
    kill -9 \$PID
  else
    echo "✅ 포트 $PORT는 비어 있습니다."
  fi

  echo "🚀 애플리케이션 시작 중..."
  cd $REMOTE_DIR
  nohup java -jar $JAR_NAME > app.log 2>&1 &
  echo "✅ 애플리케이션이 백그라운드에서 실행되었습니다."
EOF

echo "🎉 [4/4] 배포 완료!"
