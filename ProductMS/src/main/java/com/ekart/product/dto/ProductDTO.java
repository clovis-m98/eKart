package com.ekart.product.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Integer productId;
    private String name;
    private String description;
    private String category;
    private String brand;
    private Double price;
    private Integer availableQuantity;
}
