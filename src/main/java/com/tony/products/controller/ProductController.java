package com.tony.products.controller;

import com.tony.products.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        product.setId(3L);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = Arrays.asList(
            new Product(1L, "Laptop", "Gaming laptop", new BigDecimal("999.99"), 10),
            new Product(2L, "Phone", "Smartphone", new BigDecimal("599.99"), 25)
        );
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        if (productId == 1L) {
            Product product = new Product(1L, "Laptop", "Gaming laptop", new BigDecimal("999.99"), 10);
            return new ResponseEntity<>(product, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
