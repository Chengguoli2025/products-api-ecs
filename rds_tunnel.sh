#!/bin/bash

# Configuration variables
EC2_USER="ec2-user"
EC2_IP="52.64.95.232"  # Current IP, will need to be updated when EC2 restarts
EC2_KEY_PATH="~/Tony/project/aws/my-app.pem"
RDS_HOST="database-1.cx0qnfjtc2ft.ap-southeast-2.rds.amazonaws.com"
RDS_PORT="5432"
LOCAL_PORT="5433"

# Create SSH tunnel
echo "Setting up SSH tunnel to RDS through EC2 jump box..."
echo "RDS will be available at localhost:$LOCAL_PORT"
echo "Press Ctrl+C to close the tunnel when finished"

# Ask for current EC2 IP if needed
read -p "Use EC2 IP [$EC2_IP]? Press Enter to accept or type a new IP: " input_ip
EC2_IP=${input_ip:-$EC2_IP}

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