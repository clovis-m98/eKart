package com.ekart.cart.entity;


import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="EK_CUSTOMER_CART")
@Data
public class CustomerCart {
    @Id
    @Column(name="CART_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer cartId;


    @Column(name="CUSTOMER_EMAIL_ID")
    private String customerEmailId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name ="cartId")
    private Set<CartProduct> cartProducts;
}
