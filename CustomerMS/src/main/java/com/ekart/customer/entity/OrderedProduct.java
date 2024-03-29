package com.ekart.customer.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="EK_ORDERED_PRODUCT")
@Data
public class OrderedProduct {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer orderedProductId;
    private Integer productId;
    private Integer quantity;
}
