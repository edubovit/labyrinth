#!/bin/bash -xeu

cd "${REPO_LOCATION:-$(pwd)}"
source .env

./gradlew --no-daemon clean
./gradlew --no-daemon build
sudo systemctl stop labyrinth.service
cp -f build/libs/labyrinth.jar "$BACKEND_DEPLOYMENT_LOCATION/labyrinth.jar"
sudo systemctl start labyrinth.service

cd ui
rm -rf dist node_modules
npm install
npm run build
rm -rf "${UI_DEPLOYMENT_LOCATION:?}"/*
cp -rf dist/. "$UI_DEPLOYMENT_LOCATION/"

curl "$PRODUCTION_HOST/api/actuator/health"
curl "$PRODUCTION_HOST/"

echo "Deployment successful!"
