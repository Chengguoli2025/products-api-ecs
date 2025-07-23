#!/bin/bash

# Script to deploy the RDS CloudFormation stack

# Default values
ENVIRONMENT="dev"
DB_NAME="productsdb"
DB_USERNAME="dbadmin"
DB_INSTANCE_CLASS="db.t3.micro"
PASSWORD_ROTATION_DAYS=30
DB_ENGINE_VERSION="17.4"
PRIVATE_SUBNET1="subnet-0ffb7ecb30cd8431d"
PRIVATE_SUBNET2="subnet-0b066ef635aebfd81"
PRIVATE_SUBNET3="subnet-09ff13fc14a20db6b"

# Parse command line arguments
while [[ $# -gt 0 ]]; do
  key="$1"
  case $key in
    --env)
      ENVIRONMENT="$2"
      shift 2
      ;;
    --subnet1)
      PRIVATE_SUBNET1="$2"
      shift 2
      ;;
    --subnet2)
      PRIVATE_SUBNET2="$2"
      shift 2
      ;;
    --subnet3)
      PRIVATE_SUBNET3="$2"
      shift 2
      ;;
    --db-name)
      DB_NAME="$2"
      shift 2
      ;;
    --username)
      DB_USERNAME="$2"
      shift 2
      ;;
    --instance-class)
      DB_INSTANCE_CLASS="$2"
      shift 2
      ;;
    --rotation-days)
      PASSWORD_ROTATION_DAYS="$2"
      shift 2
      ;;
    --engine-version)
      DB_ENGINE_VERSION="$2"
      shift 2
      ;;
    *)
      echo "Unknown option: $1"
      exit 1
      ;;
  esac
done

# Validate environment
if [[ "$ENVIRONMENT" != "dev" && "$ENVIRONMENT" != "staging" && "$ENVIRONMENT" != "prod" ]]; then
  echo "Error: Environment must be one of: dev, staging, prod"
  exit 1
fi

# Stack name
STACK_NAME="products-api-rds-$ENVIRONMENT"

# Check if stack exists and is in ROLLBACK_COMPLETE state
STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[0].StackStatus" --output text 2>/dev/null || echo "STACK_NOT_FOUND")

if [ "$STACK_STATUS" == "ROLLBACK_COMPLETE" ]; then
  echo "Stack is in ROLLBACK_COMPLETE state. Deleting stack before redeploying..."
  aws cloudformation delete-stack --stack-name $STACK_NAME
  echo "Waiting for stack deletion to complete..."
  aws cloudformation wait stack-delete-complete --stack-name $STACK_NAME
  echo "Stack deleted successfully."
fi

# Deploy the stack
echo "Deploying RDS stack: $STACK_NAME"
echo "Environment: $ENVIRONMENT"
echo "Database Name: $DB_NAME"
echo "Username: $DB_USERNAME"
echo "Instance Class: $DB_INSTANCE_CLASS"
echo "Engine Version: $DB_ENGINE_VERSION"
echo "Password Rotation Days: $PASSWORD_ROTATION_DAYS"
echo "Subnets: $PRIVATE_SUBNET1, $PRIVATE_SUBNET2, $PRIVATE_SUBNET3"

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

aws cloudformation deploy \
  --stack-name $STACK_NAME \
  --template-file "$SCRIPT_DIR/rds-template.yaml" \
  --parameter-overrides \
    Environment=$ENVIRONMENT \
    DBName=$DB_NAME \
    DBUsername=$DB_USERNAME \
    DBInstanceClass=$DB_INSTANCE_CLASS \
    DBEngineVersion=$DB_ENGINE_VERSION \
    PasswordRotationDays=$PASSWORD_ROTATION_DAYS \
    PrivateSubnet1Id=$PRIVATE_SUBNET1 \
    PrivateSubnet2Id=$PRIVATE_SUBNET2 \
    PrivateSubnet3Id=$PRIVATE_SUBNET3 \
  --capabilities CAPABILITY_IAM

# Check if deployment was successful
if [ $? -eq 0 ]; then
  echo "RDS stack deployment successful!"
  
  # Get outputs
  echo "Retrieving stack outputs..."
  
  DB_ENDPOINT=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[0].Outputs[?OutputKey=='DatabaseEndpoint'].OutputValue" --output text)
  DB_SECRET_ARN=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[0].Outputs[?OutputKey=='DatabaseSecretArn'].OutputValue" --output text)
  JDBC_URL=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[0].Outputs[?OutputKey=='JDBCConnectionString'].OutputValue" --output text)
  
  echo ""
  echo "Database Endpoint: $DB_ENDPOINT"
  echo "Database Secret ARN: $DB_SECRET_ARN"
  echo "JDBC Connection String: $JDBC_URL"
  echo ""
  echo "To retrieve the database credentials:"
  echo "aws secretsmanager get-secret-value --secret-id $DB_SECRET_ARN --query SecretString --output text"
else
  echo "RDS stack deployment failed!"
fi