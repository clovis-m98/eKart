package com.ekart.payment.entity;

import com.ekart.payment.dto.TransactionStatus;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="EK_TRANSACTION")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;
    private Integer orderId;
    private Integer cardId;
    private Double totalPrice;
    private LocalDateTime transactionDate;
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;
}
