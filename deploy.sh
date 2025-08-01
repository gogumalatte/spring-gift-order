echo "> 배포를 시작합니다."

BUILD_PATH=$(ls -t /home/ubuntu/build/libs/*.jar | head -n 1)
JAR_NAME=$(basename $BUILD_PATH)
echo "> 빌드 파일 이름: $JAR_NAME"
echo "> 빌드 파일 경로: $BUILD_PATH"

CURRENT_PID=$(pgrep -f $JAR_NAME)
echo "> 현재 실행 중인 애플리케이션 PID: $CURRENT_PID"

if [ -z $CURRENT_PID ]
then
  echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

DEPLOY_PATH=/home/ubuntu/
DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME

echo "> 새 애플리케이션을 배포합니다."
cp $BUILD_PATH $DEPLOY_PATH

LOG_PATH="/home/ubuntu/log"
if [ ! -d "$LOG_PATH" ]; then
  mkdir "$LOG_PATH"
fi

echo "> $DEPLOY_JAR 를 실행합니다."

nohup java -jar -Dspring.profiles.active=prod $DEPLOY_JAR >> $LOG_PATH/deploy.log 2>&1 &