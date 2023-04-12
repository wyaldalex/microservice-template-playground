package com.tudux.ProductService.service;

import com.tudux.ProductService.entity.Product;
import com.tudux.ProductService.model.ProductRequest;
import com.tudux.ProductService.model.ProductResponse;
import com.tudux.ProductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.*;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;
    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding Product..");

        Product product = Product.builder()
                .productName(productRequest.getName())
                .quantity(productRequest.getQuantity())
                .price(productRequest.getPrice()).build();
        productRepository.save(product);

        log.info("Product Created");
        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {
        log.info("Get the product for id: {}", productId);
        Product product
                = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product with given id not found"));
        ProductResponse productResponse = new ProductResponse();

        copyProperties(product,productResponse);
        return productResponse;
    }
}
