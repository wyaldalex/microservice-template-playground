package com.tudux.OrderService.service;

import com.tudux.OrderService.model.OrderRequest;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);
}
