#!/bin/bash
set -e

APP_NAME="notification-service"
DEPLOY_DIR="/deployments/$APP_NAME"
APP_YML="$DEPLOY_DIR/application.yml"
APP_YML_DIR="./email-service/src/main/resources/"

echo "🚀 Packaging app for deployment..."

cd "$DEPLOY_DIR"

mv "$APP_YML" "$APP_YML_DIR"

echo "📦 Building application JAR with Maven..."
mvn clean package -Dskiptests

echo "✅ Service packaged successfully!"
