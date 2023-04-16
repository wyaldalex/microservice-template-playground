package com.tudux.OrderService.service;

import com.tudux.OrderService.entity.Order;
import com.tudux.OrderService.exception.CustomException;
import com.tudux.OrderService.external.client.PaymentService;
import com.tudux.OrderService.external.client.ProductService;
import com.tudux.OrderService.external.client.request.PaymentRequest;
import com.tudux.OrderService.external.response.PaymentResponse;
import com.tudux.OrderService.model.OrderRequest;
import com.tudux.OrderService.model.OrderResponse;
import com.tudux.OrderService.model.ProductResponse;
import com.tudux.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public long placeOrder(OrderRequest orderRequest) {
        log.info("Placing Order Request {}", orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(),orderRequest.getQuantity());

        log.info("Order Places successfully with Order Id {}");
        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();

        order = orderRepository.save(order);

        log.info("Calling Payment Service to complete the payment");
        PaymentRequest paymentRequest =
                PaymentRequest.builder()
                        .orderId(order.getId())
                        .paymentMode(orderRequest.getPaymentMode())
                        .amount(orderRequest.getTotalAmount())
                        .build();

        String orderStatus = null;

        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done Succesfully. Changing the Order status");
            orderStatus = "PLACED";
        } catch (Exception e) {
            log.error("Error ocurred in payment. Changing to failed");
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order Placed succesfully with order Id {}", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order details for Order Id: {}", orderId);

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(()-> new CustomException("Order Not Found for that orderId: " +orderId
                                , "ORDER_NOT_FOUND", 404
                                ));

        log.info("Invoking Product service to fetch info for Product {} in Order {}", order.getProductId(), order.getId());
        ProductResponse productResponse = restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/" + orderId,
                ProductResponse.class
        );

        log.info("Invoking Order Service to fetch info for Order Id {} ", order.getId());
        PaymentResponse paymentResponse = restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payment/order/" + orderId,
                PaymentResponse.class
        );

        OrderResponse.ProductDetails productDetails =
                OrderResponse.ProductDetails.builder()
                        .productName(productResponse.getProductName())
                        .productId(productResponse.getProductId())
                        .build();


        OrderResponse.PaymentDetails paymentDetails =
                OrderResponse.PaymentDetails.builder()
                        .paymentId(paymentResponse.getPaymentId())
                        .paymentStatus(paymentResponse.getStatus())
                        .paymentDate(paymentResponse.getPaymentDate())
                        .paymentMode(paymentResponse.getPaymentMode())
                        .build();

        OrderResponse orderResponse =
                OrderResponse.builder()
                        .orderId(order.getId())
                        .orderStatus(order.getOrderStatus())
                        .amount(order.getAmount())
                        .orderDate(order.getOrderDate())
                        .productDetails(productDetails)
                        .paymentDetails(paymentDetails)
                        .build();

        return orderResponse;
    }
}
