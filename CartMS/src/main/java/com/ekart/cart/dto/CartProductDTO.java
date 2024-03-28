package com.ekart.cart.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Data
public class CartProductDTO {
    private Integer cartProductId;
    @Valid
    private ProductDTO product;
    @PositiveOrZero(message = "{product.invalid.quantity}")
    private Integer quantity;
}
