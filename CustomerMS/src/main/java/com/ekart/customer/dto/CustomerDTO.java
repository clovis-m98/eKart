package com.ekart.customer.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CustomerDTO {
	@NotNull(message = "{email.absent}")
	@Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",
			message = "{invalid.email.format}")
	private String emailId;
	@Pattern(regexp = "([A-Za-z])+(\\s[A-Za-z]+)*", message = "{customer.invalid.name}")
	private String name;
	@NotNull(message = "{password.absent}")
	@Pattern(regexp = ".*[A-Z]+.*", message = "{invalid.password.format.uppercase}")
	@Pattern(regexp = ".*[a-z]+.*", message = "{invalid.password.format.lowercase}")
	@Pattern(regexp = ".*[0-9]+.*", message = "{invalid.password.format.number}")
	@Pattern(regexp = ".*[^a-zA-Z-0-9].*", message = "{invalid.password.format.specialCharacter}")
	private String password;
	private String newPassword;
	@Size(max=10, min = 10, message = "{customer.invalid.phoneNumber}")
	@Pattern(regexp = "[0-9]+", message = "{customer.invalid.phoneNumber}")
	private String phoneNumber;
	@NotNull(message = "{customer.address.absent}")
	private String address;
}
