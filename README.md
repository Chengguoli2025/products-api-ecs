# products-api-ecs

## Run application when local profile
./gradlew bootRun --args='--spring.profiles.active=local'

## Local build and push docker image to ecr
docker build -t products-api .    
docker tag products-api 427260910901.dkr.ecr.ap-southeast-2.amazonaws.com/products-api:latest   
docker push 427260910901.dkr.ecr.ap-southeast-2.amazonaws.com/products-api:latest

## Rotate rds password
aws secretsmanager cancel-rotate-secret --secret-id arn:aws:secretsmanager:ap-southeast-2:427260910901:secret:prod/products-api/db-credentials-DxfHBr
aws secretsmanager rotate-secret --secret-id arn:aws:secretsmanager:ap-southeast-2:427260910901:secret:prod/products-api/db-credentials-DxfHBr

You can connect to the database using these credentials with any PostgreSQL client. For example:

```bash
# Using psql command line
psql -h products-api-rds-prod-db.cx0qnfjtc2ft.ap-southeast-2.rds.amazonaws.com -p 5432 -U dbadmin -d productsdb

# Using JDBC connection string
jdbc:postgresql://products-api-rds-prod-db.cx0qnfjtc2ft.ap-southeast-2.rds.amazonaws.com:5432/productsdb
```

## VPC and Subnets Design

Carve your 10.0.0.0/16 into clearly non-overlapping blocks per environment, then place subnets inside each block.

### Environment Blocks (No Overlap)

Give each env a big, dedicated slice so you can add subnets later without collisions:

- **Dev**: 10.0.0.0/18 (covers 10.0.0.0–10.0.63.255)
- **Staging**: 10.0.64.0/18 (covers 10.0.64.0–10.0.127.255)
- **Prod**: 10.0.128.0/18 (covers 10.0.128.0–10.0.191.255)
- **Future expansion**: 10.0.192.0/18 (reserved)

Each /18 = 16,384 IPs total—plenty of headroom.

### Subnets Inside Each Environment

Use /24 subnets (256 total, 251 usable after AWS reserves 5). Below shows 2 AZs (you can add a 3rd set later).

#### Dev (10.0.0.0/18)
- Public-a: 10.0.0.0/24
- Public-b: 10.0.1.0/24
- Private-a: 10.0.2.0/24
- Private-b: 10.0.3.0/24

#### Staging (10.0.64.0/18)
- Public-a: 10.0.64.0/24
- Public-b: 10.0.65.0/24
- Private-a: 10.0.66.0/24
- Private-b: 10.0.67.0/24

#### Prod (10.0.128.0/18)
- Public-a: 10.0.128.0/24
- Public-b: 10.0.129.0/24
- Private-a: 10.0.130.0/24
- Private-b: 10.0.131.0/24

### Routing & HA Tips

- Create separate route tables per subnet type (public vs private)
- Public subnets route 0.0.0.0/0 to the IGW
- Private subnets route 0.0.0.0/0 to a NAT Gateway per AZ (for resilience)
- Tag subnets clearly: `Env=dev|stg|prod`, `Tier=public|private`, `AZ=a|b`

### Why This Works

- Each environment lives in its own /18 island → impossible to overlap
- You get at least 2 public + 2 private subnets per env now, and you can add many more later without touching other envs
- Using /24 keeps IP management straightforward and avoids overly huge broadcast domains


### Health check failed when deploy new service
- HealthCheckPath: /health
- HealthCheckIntervalSeconds: 15
- HealthCheckTimeoutSeconds: 10
- HealthyThresholdCount: 2
- UnhealthyThresholdCount: 8