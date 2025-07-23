# RDS with Auto-Generated Password in Secrets Manager

This CloudFormation template creates an RDS MySQL instance with the following features:
- Password automatically generated and stored in AWS Secrets Manager
- Password rotation configured to occur automatically at specified intervals
- Uses the specified VPC and security group

## Parameters

- **Environment**: The deployment environment (dev, staging, prod)
- **VpcId**: The VPC ID (default: vpc-03b72cde5edbd5472)
- **PrivateSubnet1Id/PrivateSubnet2Id**: Subnet IDs for the RDS instance
- **DBInstanceClass**: The RDS instance type (default: db.t3.micro)
- **DBName**: The name of the database (default: productsdb)
- **DBUsername**: The admin username (default: admin)
- **PasswordRotationDays**: Number of days before password rotation (default: 30)

## Security

- The database password is automatically generated and stored in AWS Secrets Manager
- Password is automatically rotated based on the specified rotation period
- The RDS instance uses the specified security group (sg-08c9481353384fe62)

## Deployment Instructions

1. Deploy the stack using AWS CLI:

```bash
aws cloudformation create-stack \
  --stack-name products-api-rds \
  --template-body file://rds-template.yaml \
  --parameters \
    ParameterKey=Environment,ParameterValue=dev \
    ParameterKey=DBName,ParameterValue=productsdb
```

2. Or deploy using AWS Console:
   - Go to CloudFormation in the AWS Console
   - Create new stack
   - Upload the rds-template.yaml file
   - Fill in the parameters as needed

## Accessing Database Credentials

The database credentials are stored in AWS Secrets Manager. You can access them in your application using:

### For AWS SDK:
```java
AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard().build();
GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId("dev/products-api/db-credentials");
GetSecretValueResult getSecretValueResult = client.getSecretValue(getSecretValueRequest);
String secret = getSecretValueResult.getSecretString();
// Parse JSON secret to get username and password
```

### For ECS Tasks:
Add the following to your task definition:

```json
"secrets": [
  {
    "name": "SPRING_DATASOURCE_USERNAME",
    "valueFrom": "arn:aws:secretsmanager:region:account-id:secret:dev/products-api/db-credentials:username::"
  },
  {
    "name": "SPRING_DATASOURCE_PASSWORD",
    "valueFrom": "arn:aws:secretsmanager:region:account-id:secret:dev/products-api/db-credentials:password::"
  }
]
```

## Integration with Existing ECS Service

To integrate this RDS instance with your existing ECS service:

1. Update your ECS task definition to include the database connection information
2. Grant the ECS task execution role permission to access the secret in Secrets Manager
3. Configure your application to use the database connection information from environment variables