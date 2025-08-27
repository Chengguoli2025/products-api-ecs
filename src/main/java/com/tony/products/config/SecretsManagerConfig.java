package com.tony.products.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import jakarta.annotation.PostConstruct;

@Configuration
@Profile("!local")
public class SecretsManagerConfig {

    @Value("${spring.cloud.aws.secretsmanager.secret-name:prod/products-api/db-credentials}")
    private String secretName;

    @Value("${spring.cloud.aws.secretsmanager.region:ap-southeast-2}")
    private String region;

    @PostConstruct
    public void loadDatabaseCredentials() {
        try {
            SecretsManagerClient client = SecretsManagerClient.builder()
                    .region(Region.of(region))
                    .build();

            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            GetSecretValueResponse response = client.getSecretValue(request);
            String secretString = response.secretString();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode secretJson = mapper.readTree(secretString);

            String username = secretJson.get("username").asText();
            String password = secretJson.get("password").asText();

            // Set system properties that will be used by Spring
            System.setProperty("DB_USERNAME", username);
            System.setProperty("DB_PASSWORD", password);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load database credentials from Secrets Manager", e);
        }
    }
}