#!/bin/bash
# This script is run before the installation of the package.
# Stops the application if it is runnning.
echo "Stopping existing notification-aservice if already running..."
pm2 stop notification-service || true
echo "Removing existing notification-service files..."
sudo rm -rf "/deployments/notification-service/*"