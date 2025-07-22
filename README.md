# products-api-ecs

## Run application when local profile
./gradlew bootRun --args='--spring.profiles.active=local'

## Local build and push docker image to ecr
docker build -t products-api .    
docker tag products-api 427260910901.dkr.ecr.ap-southeast-2.amazonaws.com/products-api:latest   
docker push 427260910901.dkr.ecr.ap-southeast-2.amazonaws.com/products-api:latest