package com.ekart.cart.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Data
public class CustomerCartDTO {
    private Integer cartId;
    @NotNull(message = "{email.absent}")
    @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+" , message = "{invalid.email.format}")
    private String customerEmailId;
    @Valid
    private Set<CartProductDTO> cartProducts;
}
