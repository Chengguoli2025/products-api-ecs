# Integrating RDS with ECS Service

This guide explains how to integrate the RDS instance created by the `rds-template.yaml` with your existing ECS service.

## Step 1: Update IAM Role

Add the following policy to your ECS task execution role to allow access to the database credentials in Secrets Manager:

```yaml
TaskExecutionRole:
  Type: AWS::IAM::Role
  Properties:
    # ... existing properties ...
    Policies:
      - PolicyName: SecretsManagerAccess
        PolicyDocument:
          Version: '2012-10-17'
          Statement:
            - Effect: Allow
              Action:
                - secretsmanager:GetSecretValue
              Resource: !ImportValue <RDS-Stack-Name>:DatabaseSecretArn
```

## Step 2: Update Task Definition

Update your ECS task definition to include the database connection information:

```yaml
TaskDefinition:
  Type: AWS::ECS::TaskDefinition
  Properties:
    # ... existing properties ...
    ContainerDefinitions:
      - Name: products-api
        # ... existing properties ...
        Environment:
          # ... existing environment variables ...
          - Name: SPRING_DATASOURCE_URL
            Value: !ImportValue <RDS-Stack-Name>:JDBCConnectionString
        Secrets:
          - Name: SPRING_DATASOURCE_USERNAME
            ValueFrom: !Sub '{{resolve:secretsmanager:${DatabaseSecretArn}:SecretString:username}}'
          - Name: SPRING_DATASOURCE_PASSWORD
            ValueFrom: !Sub '{{resolve:secretsmanager:${DatabaseSecretArn}:SecretString:password}}'
```

## Step 3: Update Application Configuration

Make sure your Spring Boot application is configured to use the database connection information from environment variables:

```properties
# application.properties or application.yml
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
```

## Step 4: Update Security Group Rules

Ensure that your ECS security group is allowed to access the RDS security group:

```yaml
RDSSecurityGroup:
  Type: AWS::EC2::SecurityGroup
  Properties:
    # ... existing properties ...
    SecurityGroupIngress:
      - IpProtocol: tcp
        FromPort: 3306
        ToPort: 3306
        SourceSecurityGroupId: !Ref ECSSecurityGroup
```

## Step 5: Deploy Updates

1. Deploy the RDS stack first:
   ```bash
   ./deploy-rds.sh --env dev
   ```

2. Update your ECS stack with the new configuration:
   ```bash
   aws cloudformation update-stack --stack-name products-api-ecs --template-body file://template-existing-vpc.yaml --parameters ...
   ```

## Troubleshooting

If your application cannot connect to the database:

1. Check that the security group rules allow traffic from the ECS tasks to the RDS instance
2. Verify that the ECS task execution role has permission to access the secret in Secrets Manager
3. Confirm that the environment variables are correctly set in the task definition
4. Check the ECS task logs for any connection errors