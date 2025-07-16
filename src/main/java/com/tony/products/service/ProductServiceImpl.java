package com.tony.products.service;

import com.tony.products.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service("SOFTWARE")
public class ProductServiceImpl implements ProductService {

    @Override
    public List<Product> getAllProducts() {
        // TODO: Implement with repository
        return Arrays.asList(
                new Product(1000L, "Laptop--11-1-", "Gaming laptop", new BigDecimal("999.99"), 10),
                new Product(2000L, "Phone", "Smartphone", new BigDecimal("599.99"), 25)
        );
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        // TODO: Implement with repository
        return Optional.empty();
    }

    @Override
    public Product createProduct(Product product) {
        // TODO: Implement with repository
        return product;
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        // TODO: Implement with repository
        return product;
    }

    @Override
    public void deleteProduct(Long id) {
        // TODO: Implement with repository
    }
}