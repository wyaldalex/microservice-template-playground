package com.tudux.PaymentService.service;

import com.tudux.PaymentService.model.PaymentRequest;
import com.tudux.PaymentService.model.PaymentResponse;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(String orderId);
}
