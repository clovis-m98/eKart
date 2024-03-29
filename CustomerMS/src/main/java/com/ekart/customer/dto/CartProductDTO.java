package com.ekart.customer.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Data
public class CartProductDTO {
	private Integer cartProductId;
	@Valid
	private ProductDTO product;
	@PositiveOrZero(message = "{cartProduct.invalid.quantity}")
	private Integer quantity;
}
