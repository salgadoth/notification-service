#!/bin/bash
set -e

# Define file path
#OUTPUT_FILE="../application.yml"

# Create file if doesn't exists
#cd /deployments/notification-service
sudo touch "../application.yml"
sudo chmod 600 "../application.yml"
sudo chown "$USER":"$USER" "../application.yml"

# Retrieve all parameters from SSM parameter store by path
PARAMS=$(aws ssm get-parameters-by-path --path "/notification_service/env" --recursive --with-decryption --query "Parameters[*].{Name:Name, Value:Value}" --output json)

# Use jq to parse and format parameters
generate_yml() {
  echo "spring:"
  echo "  data:"
  echo "    mongodb:"
  echo "      username: $(echo "$PARAMS" | jq -r '.[] | select(.Name=="/notification_service/env/MONGODB_USERNAME") | .Value')"
  echo "      password: $(echo "$PARAMS" | jq -r '.[] | select(.Name=="/notification_service/env/MONGODB_PASSWORD") | .Value')"
  echo "      uri: $(echo "$PARAMS" | jq -r '.[] | select(.Name=="/notification_service/env/MONGODB_URI") | .Value')"
  echo "  rabbitmq:"
  echo "    host: $(echo "$PARAMS" | jq -r '.[] | select(.Name=="/notification_service/env/RABBITMQ_ADDR") | .Value')"
  echo "    username: $(echo "$PARAMS" | jq -r '.[] | select(.Name=="/notification_service/env/RABBITMQ_USERNAME") | .Value')"
  echo "    password: $(echo "$PARAMS" | jq -r '.[] | select(.Name=="/notification_service/env/RABBITMQ_PASSWORD") | .Value')"
  echo ""
  echo "google:"
  echo "  gmail:"
  echo "    credentials-file-path: \"$(echo "$PARAMS" | jq -r '.[] | select(.Name=="/notification_service/env/GOOGLE_CREDENTIALS_FILE_PATH") | .Value')\""
  echo "    sender-email: $(echo "$PARAMS" | jq -r '.[] | select(.Name=="/notification_service/env/GOOGLE_EMAIL_SENDER_ADDRESS") | .Value')"
}

# Write to file
generate_yml > "../application.yml"

echo "application.yml successfully generated."