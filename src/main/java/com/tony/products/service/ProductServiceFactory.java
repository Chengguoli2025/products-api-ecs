package com.tony.products.service;

import com.tony.products.enums.ProductType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor

public class ProductServiceFactory {

    private final Map<String, ProductService> productServiceMap;

    /*@Autowired
    public ProductServiceFactory(Map<String, ProductService> productServiceMap) {
        this.productServiceMap = productServiceMap;
    }*/

    public ProductService getProductService(ProductType productType) {
        return productServiceMap.get(productType.name());
    }
}