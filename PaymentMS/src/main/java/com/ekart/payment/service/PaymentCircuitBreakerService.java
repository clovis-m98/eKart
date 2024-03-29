package com.ekart.payment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentCircuitBreakerService {

    @Autowired
    private RestTemplate template;

    // Add necessary CircuitBreaker annotation
    public void updateOrderAfterPayment(Integer orderId,String transactionStatus) throws Exception {
        template.put("http://CUSTOMERMS" + "/customerorder-api/order/"+orderId+"/update/order-status", transactionStatus);
    }
}
