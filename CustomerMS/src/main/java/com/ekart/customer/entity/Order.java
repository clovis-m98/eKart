package com.ekart.customer.entity;

import com.ekart.customer.dto.OrderStatus;
import com.ekart.customer.dto.PaymentThrough;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "EK_ORDER")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;
    private String customerEmailId;
    private LocalDateTime dateOfOrder;
    private Double discount;
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PaymentThrough paymentThrough;
    private LocalDateTime dateOfDelivery;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "orderId")
    private List<OrderedProduct> orderedProducts;
    private String deliveryAddress;
}
