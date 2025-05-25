#!/bin/bash
set -e

# Define file path
OUTPUT_FILE="email-service/src/main/resources/application.yml"

# Create file if doesn't exists
sudo touch "$OUTPUT_FILE"
sudo chmod 600 "$OUTPUT_FILE"
sudo chown "$USER":"$USER" "$OUTPUT_FILE"

# Define file path
OUTPUT_FILE="email-service/src/main/resources/application.yml"

# Retrieve all parameters from SSM parameter store by path
PARAMS=$(aws ssm get-parameters-by-path --path "/notification-application" --recursive --with-decryption --query "Parameters[*].{Name:Name, Value:Value" --output json)

# Use jq to parse and format parameters
generate_yml() {
  echo "spring:"
  echo "  data:"
  echo "    mongodb:"
    echo "      uri: $(echo "$PARAMS" | jq -r '.[] | select(.Name=="/email-service/spring/data/mongodb/uri") | .Value')"
    echo "  rabbitmq:"
    echo "    host: $(echo "$PARAMS" | jq -r '.[] | select(.Name=="/email-service/spring/rabbitmq/host") | .Value')"
    echo "    username: $(echo "$PARAMS" | jq -r '.[] | select(.Name=="/email-service/spring/rabbitmq/username") | .Value')"
    echo "    password: $(echo "$PARAMS" | jq -r '.[] | select(.Name=="/email-service/spring/rabbitmq/password") | .Value')"
    echo ""
    echo "google:"
    echo "  gmail:"
    echo "    credentials-file-path: \"$(echo "$PARAMS" | jq -r '.[] | select(.Name=="/email-service/google/gmail/credentials-file-path") | .Value')\""
    echo "    sender-email: $(echo "$PARAMS" | jq -r '.[] | select(.Name=="/email-service/google/gmail/sender-email") | .Value')"
}

# Write to file
generate_yml > "$OUTPUT_FILE"

echo "application.yml successfully generated."