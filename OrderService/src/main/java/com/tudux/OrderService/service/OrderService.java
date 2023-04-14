package com.tudux.OrderService.service;

import com.tudux.OrderService.model.OrderRequest;
import com.tudux.OrderService.model.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
