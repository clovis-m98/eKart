package com.ekart.customer.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class OrderDTO {
	private Integer orderId;
	@NotNull(message = "{email.absent}")
	@Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",
			message = "{invalid.email.format}")
	private String customerEmailId;
	private LocalDateTime dateOfOrder;
	private Double totalPrice;
	private String orderStatus;
	private Double discount;
	@NotNull(message = "{order.paymentThrough.absent}")
	@Pattern(regexp = "(DEBIT_CARD|CREDIT_CARD)", message = "{order.paymentThrough.invalid}")
	private String paymentThrough;
	@NotNull(message = "{order.dateOfDelivery.absent}")
	@Future(message = "{order.dateOfDelivery.invalid}")
	private LocalDateTime dateOfDelivery;
	private String deliveryAddress;
	private List<OrderedProductDTO> orderedProducts;
}
