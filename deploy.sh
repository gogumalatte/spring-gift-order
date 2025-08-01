REPOSITORY=/home/ubuntu

echo "> 프로젝트 저장소로 이동합니다."
cd $REPOSITORY

echo "> Gradle 실행 권한을 추가합니다."
chmod +x ./gradlew

echo "> 프로젝트를 빌드합니다."
./gradlew build

echo "> 기존 build 폴더를 삭제합니다."
rm -rf ~/build

echo "> 신규 build 폴더를 생성하고 jar 파일을 복사합니다."
mkdir ~/build
cp $REPOSITORY/build/libs/*.jar ~/build/

JAR_PATH=$(ls -tr ~/build/*.jar | tail -n 1)
JAR_NAME=$(basename $JAR_PATH)

echo "> 빌드 파일 이름: $JAR_NAME"
echo "> 빌드 파일 경로: $JAR_PATH"

CURRENT_PID=$(pgrep -f $JAR_NAME)
echo "> 현재 실행 중인 애플리케이션 PID: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
  echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션을 배포합니다."
nohup java -jar \
    -Dspring.profiles.active=prod \
    $JAR_PATH > $REPOSITORY/log/deploy.log 2>&1 &

echo "> 배포가 완료되었습니다."