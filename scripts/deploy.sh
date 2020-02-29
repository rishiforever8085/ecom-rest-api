#!/bin/sh
echo "Starting deployment"

cd /home/ec2-user/projects/ecom-rest-api
echo "moved to project directory $PWD"

git stash
echo "stashed local changes"

git pull
echo "pulled latest changes"

git stash pop
echo "stash poped"

./gradlew clean bootJar
echo "built the latest artifact"

cp /home/ec2-user/artifacts/ecom-rest-api.jar /home/ec2-user/artifacts/ecom-rest-api-$date.jar
echo "back up old artifact"

mv /home/ec2-user/projects/ecom-rest-api/build/libs/ecom-rest-api-1.0-SNAPSHOT.jar /home/ec2-user/artifacts/ecom-rest-api.jar
echo "moved the artifact to artifacts folder"

kill -f 'java.*ecom-rest-api'
echo "killed current java process"

nohup java -Dspring.profiles.active=prod -jar /home/ec2-user/artifacts/ecom-rest-api.jar &
echo "started new java process"
