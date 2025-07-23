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
