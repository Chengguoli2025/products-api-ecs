package com.tony.products;

import com.tony.products.config.DatabaseCredentialsLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductApiApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ProductApiApplication.class);
        app.addListeners(new DatabaseCredentialsLoader());
        app.run(args);
    }
}