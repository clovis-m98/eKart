package com.ekart.payment.dto;

import lombok.Data;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Integer transactionId;
    @Valid
    private OrderDTO order;
    @Valid
    private CardDTO card;
    private Double totalPrice;
    private LocalDateTime transactionDate;
    private TransactionStatus transactionStatus;
}
