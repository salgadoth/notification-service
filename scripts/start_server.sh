APP_NAME="notification-service"
DEPLOY_DIR="/deployments/$APP_NAME"
ECOSYSTEM_FILE="$DEPLOY_DIR/ecosystem.config.js"

cd "$DEPLOY_DIR" || exit

echo "🔁 Restarting application with PM2..."
pm2 delete "$APP_NAME" || true
pm2 start "$ECOSYSTEM_FILE"
pm2 save

echo "✅ $APP_NAME deployed and running via PM2"