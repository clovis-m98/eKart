package com.ekart.customer.dto;

import lombok.Data;

import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
public class CustomerCartDTO {
	private Integer cartId;
	@NotNull(message = "{customerEmail.absent}")
	@Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+" ,
			message = "{invalid.customerEmail.format}")
	private String customerEmailId;
	@Valid
	private Set<CartProductDTO> cartProducts;
}
