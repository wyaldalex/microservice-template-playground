package com.tudux.ProductService.service;

import com.tudux.ProductService.model.ProductRequest;
import com.tudux.ProductService.model.ProductResponse;

public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}
