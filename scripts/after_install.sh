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

echo "📁 Copying the application.yml file to correct dir..."

sudo cp "$APP_YML" "$APP_YML_DIR"

echo "🛑 Altering permissions of deployment dir for the current user..."

# Very careful when altering this, could potentially do unrecoverable damage,
# have to explicitly declare the target user, since CodeDeploy agent
# is running on a different environment context.
sudo chown -R "admin":"admin" "$DEPLOY_DIR"
cd "$DEPLOY_DIR"

echo "🚀 Packaging app for deployment..."

echo "📦 Building application JAR with Maven..."
$MVN_HOME/bin/mvn clean package -Dskiptests

echo "✅ Service packaged successfully!"
