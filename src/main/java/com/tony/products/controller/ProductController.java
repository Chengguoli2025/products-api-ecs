package com.tony.products.controller;

import com.tony.products.enums.ProductType;
import com.tony.products.model.Product;
import com.tony.products.service.ProductService;
import com.tony.products.service.ProductServiceFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductServiceFactory productServiceFactory;

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody Product product,
            @RequestParam(name = "product_type") final ProductType productType) {
        ProductService productService = productServiceFactory.getProductService(productType);
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(name = "product_type") final ProductType productType
            )
    {
        ProductService productService = productServiceFactory.getProductService(productType);
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long productId,
            @RequestParam(name = "product_type") final ProductType productType) {
        ProductService productService = productServiceFactory.getProductService(productType);
        return productService.getProductById(productId)
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody Product product,
            @RequestParam(name = "product_type") final ProductType productType) {
        ProductService productService = productServiceFactory.getProductService(productType);
        Product updatedProduct = productService.updateProduct(productId, product);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId,
            @RequestParam(name = "product_type") final ProductType productType) {
        ProductService productService = productServiceFactory.getProductService(productType);
        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
