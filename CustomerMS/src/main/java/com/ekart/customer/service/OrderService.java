package com.ekart.customer.service;

import com.ekart.customer.dto.OrderDTO;
import com.ekart.customer.dto.OrderStatus;
import com.ekart.customer.dto.PaymentThrough;

import java.util.List;

public interface OrderService {
    Integer placeOrder(OrderDTO orderDTO) throws Exception;
    OrderDTO getOrderDetails (Integer orderId) throws Exception;
    List<OrderDTO> findOrdersByCustomerEmailId(String emailId) throws Exception;
    void updateOrderStatus( Integer orderId , OrderStatus orderStatus) throws Exception;
    void updatePaymentThrough( Integer orderId , PaymentThrough paymentThrough) throws Exception;

}
