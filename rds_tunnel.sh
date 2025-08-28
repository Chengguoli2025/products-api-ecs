#!/bin/bash

# Configuration variables
EC2_USER="ec2-user"
EC2_INSTANCE_ID="i-06d05987f082b60c9"  # Replace with your EC2 instance ID
EC2_KEY_PATH="~/Tony/project/aws/my-app.pem"
#RDS_HOST="products-api-rds-dev-db.cx0qnfjtc2ft.ap-southeast-2.rds.amazonaws.com"
RDS_HOST="products-api-rds-dev-db.cx0qnfjtc2ft.ap-southeast-2.rds.amazonaws.com"
RDS_PORT="5432"
LOCAL_PORT="5433"

# Get EC2 public IP dynamically
echo "Getting EC2 public IP..."
EC2_IP=$(aws ec2 describe-instances \
  --instance-ids $EC2_INSTANCE_ID \
  --query 'Reservations[0].Instances[0].PublicIpAddress' \
  --output text)

if [ "$EC2_IP" = "None" ] || [ -z "$EC2_IP" ]; then
  echo "Error: Could not get public IP for instance $EC2_INSTANCE_ID"
  echo "Make sure the instance is running and has a public IP"
  exit 1
fi

echo "Found EC2 public IP: $EC2_IP"

# Create SSH tunnel
echo "Setting up SSH tunnel to RDS through EC2 jump box..."
echo "RDS will be available at localhost:$LOCAL_PORT"
echo "Press Ctrl+C to close the tunnel when finished"

# Fix permissions on the key file
echo "Setting correct permissions on SSH key..."
#chmod 600 $EC2_KEY_PATH

# Establish SSH tunnel
ssh -i $EC2_KEY_PATH -N -L $LOCAL_PORT:$RDS_HOST:$RDS_PORT $EC2_USER@$EC2_IP

# Connection details for DBeaver:
# Host: localhost
# Port: $LOCAL_PORT
# Database: your_database_name
# Username: your_rds_username
# Password: your_rds_password