package com.ekart.cart.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "EK_CART_PRODUCT")
@Data
public class CartProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartProductId;
    private Integer productId;
    private Integer quantity;
}
