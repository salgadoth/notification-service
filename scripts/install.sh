#!/bin/bash
set -e

APP_NAME="notification-service"
DEPLOY_DIR="/deployments/$APP_NAME"

echo "🚀 Packaging app for deployment..."

cd "$DEPLOY_DIR"

echo "📦 Building application JAR with Maven..."
mvn clean package -Dskiptests

echo "✅ Service packaged successfully!"
