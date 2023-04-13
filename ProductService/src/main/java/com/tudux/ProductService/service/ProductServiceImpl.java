package com.tudux.ProductService.service;

import com.tudux.ProductService.entity.Product;
import com.tudux.ProductService.exception.ProductServiceCustomException;
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
                .orElseThrow(() ->
                        new ProductServiceCustomException("Product with given id not found", "PRODUCT_NOT_FOUND"));
        ProductResponse productResponse = new ProductResponse();

        copyProperties(product,productResponse);
        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce Quantity {} for Id: {} ", quantity, productId);

        Product product =
                productRepository.findById(productId).orElseThrow(() -> new ProductServiceCustomException(
                        "Product with a given Id not found",
                        "PRODUCT_NOT_FOUND"
                ));

        if(product.getQuantity() < quantity) {
            throw new ProductServiceCustomException(
                    "Produce does not have sufficient Quantity",
                    "NOT_SUFFICIENT_QUANTITY");
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
        log.info("Successfully reduced Quantity {} for Id: {} ", quantity, productId);

    }
}
