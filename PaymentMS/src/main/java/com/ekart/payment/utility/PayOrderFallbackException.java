package com.ekart.payment.utility;

import java.io.Serial;

public class PayOrderFallbackException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    public PayOrderFallbackException(String message) {
        super(message);
    }
}
