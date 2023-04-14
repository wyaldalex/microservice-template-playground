package com.tudux.PaymentService.service;

import com.tudux.PaymentService.model.PaymentRequest;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);
}
