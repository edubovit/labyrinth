#!/bin/bash -xeu

REVISION=${REVISION:-master}
REPO_LOCATION=${REPO_LOCATION:-$(pwd)}

cd "${REPO_LOCATION:-$(pwd)}"
source .env

git reset HEAD --hard
git clean -fd
git fetch --all
git checkout origin/"$REVISION"

./gradlew --no-daemon clean
./gradlew --no-daemon build
sudo systemctl stop labyrinth.service
cp -f build/libs/labyrinth*.jar "$BACKEND_DEPLOYMENT_LOCATION"/labyrinth.jar
sudo systemctl start labyrinth.service

cd ui
npm install
npm run build
rm -rf "$UI_DEPLOYMENT_LOCATION:?"/*
cp -rf dist/* "$UI_DEPLOYMENT_LOCATION"/

echo "Deployment successful!"
