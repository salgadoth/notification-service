#!/bin/bash
set -e

APP_NAME="notification-service"
DEPLOY_DIR="/deployments/$APP_NAME"
APP_YML="application.yml"
APP_YML_DIR="./email-service/src/main/resources/"

echo "🚀 Packaging app for deployment..."

sudo chown -R "$USER":"$USER" "$DEPLOY_DIR"
cd "$DEPLOY_DIR"

sudo mv "$APP_YML" "$APP_YML_DIR"

echo "📦 Building application JAR with Maven..."
mvn clean package -Dskiptests

echo "✅ Service packaged successfully!"
