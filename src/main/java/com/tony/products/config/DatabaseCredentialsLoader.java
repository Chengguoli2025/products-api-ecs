package com.tony.products.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.HashMap;
import java.util.Map;

public class DatabaseCredentialsLoader implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        System.out.println("DatabaseCredentialsLoader: Loading database credentials from Secrets Manager");

        ConfigurableEnvironment environment = event.getEnvironment();

        // Skip if running locally
        if ("true".equals(System.getenv("SKIP_SECRETS_MANAGER"))) {
            System.out.println("Skipping Secrets Manager - using local configuration");
            return;
        }

        try {
            String secretName = environment.getProperty("spring.cloud.aws.secretsmanager.secret-name", "dev/products-api/db-credentials");
            String region = environment.getProperty("spring.cloud.aws.secretsmanager.region", "ap-southeast-2");

            System.out.println("Loading secret: " + secretName + " from region: " + region);

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

            System.out.println("Loaded username: " + username);
            System.out.println("Loaded password: [HIDDEN]");

            // Add properties to Spring environment
            Map<String, Object> properties = new HashMap<>();
            properties.put("spring.datasource.username", username);
            properties.put("spring.datasource.password", password);

            MapPropertySource propertySource = new MapPropertySource("secretsManager", properties);
            environment.getPropertySources().addFirst(propertySource);

            System.out.println("Database credentials loaded successfully from Secrets Manager");

        } catch (Exception e) {
            System.err.println("Failed to load database credentials from Secrets Manager: " + e.getMessage());
            e.printStackTrace();
            // Don't throw exception to allow fallback to configured values
        }
    }
}