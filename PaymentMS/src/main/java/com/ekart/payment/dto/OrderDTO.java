package com.ekart.payment.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class OrderDTO {
    @NotNull(message = "{transaction.orderId.notPresent}")
    private Integer orderId;
    private String customerEmailId;
    private LocalDateTime dateOfOrder;
    private Double totalPrice;
    private Double discount;
    private String orderStatus;
    private String paymentThrough;
}
