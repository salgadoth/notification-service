#!/bin/bash
# This script is run before the installation of the package.
# Stops the application if it is runnning.
echo "Stopping existing notification-application if already running..."
pm2 stop notification-application || true
echo "Removing existing notification-application files..."
sudo rm -rf "/deployments/notification-application"