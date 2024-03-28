package com.ekart.cart.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class ProductDTO {
    @NotNull(message = "{product.id.absent}")
    private Integer productId;
    private String name;
    private String description;
    private String category;
    private String brand;
    private Double price;
    private Integer availableQuantity;
}
