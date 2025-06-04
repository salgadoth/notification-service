#!/bin/bash
set -e

SDK_CANDIDATES_HOME=/home/admin/.sdkman/candidates/
JAVA_HOME=$SDK_CANDIDATES_HOME/java/22.0.2-oracle
MVN_HOME=$SDK_CANDIDATES_HOME/maven/3.9.9
export PATH=$JAVA_HOME/bin:$MVN_HOME/bin:$PATH

APP_NAME="notification-service"
DEPLOY_DIR="/deployments/$APP_NAME"
APP_YML="application.yml"
APP_YML_DIR="./email-service/src/main/resources/"

echo "🛑 Altering permissions of deployment dir for the current user..."

# Very careful when altering this, could potentially destroy whole instance.
sudo chown -R "$USER":"$USER" "$DEPLOY_DIR"
cd "$DEPLOY_DIR"

echo "🚀 Packaging app for deployment..."

sudo cp "$APP_YML" "$APP_YML_DIR"

echo "📦 Building application JAR with Maven..."
/home/admin/.sdkman/candidates/maven/3.9.9/bin/mvn clean package -Dskiptests

echo "✅ Service packaged successfully!"
