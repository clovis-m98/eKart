package com.ekart.customer.service;

import com.ekart.customer.dto.*;
import com.ekart.customer.entity.Order;
import com.ekart.customer.entity.OrderedProduct;
import com.ekart.customer.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service(value = "orderService")
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    Environment environment;
    @Autowired
    private CustomerService customerService;
    @Override
    public Integer placeOrder(OrderDTO orderDTO) throws Exception {
        CustomerDTO customerDTO =customerService.getCustomerByEmailId(orderDTO.getCustomerEmailId());
        if(customerDTO.getAddress().isBlank() || customerDTO.getAddress() == null)
            throw new Exception(environment.getProperty("OrderService.ADDRESS_NOT_AVAILABLE"));
        Order order=new Order();
        order.setDeliveryAddress(customerDTO.getAddress());
        order.setCustomerEmailId(orderDTO.getCustomerEmailId());
        order.setDateOfDelivery(orderDTO.getDateOfDelivery());
        order.setDateOfOrder(LocalDateTime.now());
        order.setPaymentThrough(PaymentThrough.valueOf(orderDTO.getPaymentThrough()));
        if(order.getPaymentThrough().equals(PaymentThrough.CREDIT_CARD))
            order.setDiscount(10.00d);
        else order.setDiscount(5.00d);
        order.setOrderStatus(OrderStatus.PLACED);
        double price= 0.0 ;
        List<OrderedProduct> orderedProducts = new ArrayList<OrderedProduct>();

        for(OrderedProductDTO orderedProductDTO : orderDTO.getOrderedProducts()) {
            if(orderedProductDTO.getProduct().getAvailableQuantity() < orderedProductDTO.getQuantity())
                throw new Exception(environment.getProperty("OrderService.INSUFFICIENT_STOCK"));
            OrderedProduct orderedProduct = new OrderedProduct();
            orderedProduct.setProductId(orderedProductDTO.getProduct().getProductId());
            orderedProduct.setQuantity(orderedProductDTO.getQuantity());
            orderedProducts.add(orderedProduct);
            price = price + orderedProductDTO.getQuantity()*orderedProductDTO.getProduct().getPrice();
        }
        order.setOrderedProducts(orderedProducts);
        order.setTotalPrice(price * ( 100 - order.getDiscount())/100);
        orderRepository.save(order);
        return order.getOrderId();
    }

    @Override
    public OrderDTO getOrderDetails(Integer orderId) throws Exception {

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Order order = optionalOrder.orElseThrow(() ->
                new Exception(environment.getProperty("OrderService.ORDER_NOT_FOUND")));

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(orderId);
        orderDTO.setCustomerEmailId(order.getCustomerEmailId());
        orderDTO.setDateOfDelivery(order.getDateOfDelivery());
        orderDTO.setDateOfOrder(order.getDateOfOrder());
        orderDTO.setPaymentThrough(order.getPaymentThrough().toString());
        orderDTO.setTotalPrice(order.getTotalPrice());
        orderDTO.setOrderStatus(order.getOrderStatus().toString());
        orderDTO.setDiscount(order.getDiscount());
        List<OrderedProductDTO> orderedProductDTOs = new ArrayList<OrderedProductDTO>();
        for(OrderedProduct orderedProduct : order.getOrderedProducts()) {
            OrderedProductDTO orderedProductDTO = new OrderedProductDTO();
            ProductDTO productDTO= new ProductDTO();
            productDTO.setProductId(orderedProduct.getProductId());
            orderedProductDTO.setOrderedProductId(orderedProduct.getOrderedProductId());
            orderedProductDTO.setQuantity(orderedProduct.getQuantity());
            orderedProductDTO.setProduct(productDTO);
            orderedProductDTOs.add(orderedProductDTO);
        }
        orderDTO.setOrderedProducts(orderedProductDTOs);
        return orderDTO;
    }

    @Override
    public void updateOrderStatus(Integer orderId, OrderStatus orderStatus) throws Exception {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Order order = optionalOrder.orElseThrow(() ->
                new Exception(environment.getProperty("OrderService.ORDER_NOT_FOUND")));
        order.setOrderStatus(orderStatus);
    }
    @Override
    public void updatePaymentThrough(Integer orderId, PaymentThrough paymentThrough) throws Exception {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Order order = optionalOrder.orElseThrow(() -> new Exception("OrderService.ORDER_NOT_FOUND"));
        if(order.getOrderStatus().equals(OrderStatus.CONFIRMED))
            throw new Exception(environment.getProperty("OrderService.TRANSACTION_ALREADY_DONE"));
        order.setPaymentThrough(paymentThrough);
    }

    @Override
    public List<OrderDTO> findOrdersByCustomerEmailId(String emailId) throws Exception {
        List<Order> orders=  orderRepository.findByCustomerEmailId(emailId);
        if(orders.isEmpty())
            throw new Exception(environment.getProperty("OrderService.NO_ORDERS_FOUND") + emailId);
        List<OrderDTO> orderDTOs = new ArrayList<>();
        for(Order order :orders) {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrderId(order.getOrderId());
            orderDTO.setCustomerEmailId(order.getCustomerEmailId());
            orderDTO.setDateOfDelivery(order.getDateOfDelivery());
            orderDTO.setDateOfOrder(order.getDateOfOrder());
            orderDTO.setPaymentThrough(order.getPaymentThrough().toString());
            orderDTO.setTotalPrice(order.getTotalPrice());
            orderDTO.setOrderStatus(order.getOrderStatus().toString());
            orderDTO.setDiscount(order.getDiscount());
            List<OrderedProductDTO> orderedProductDTOs = new ArrayList<OrderedProductDTO>();
            for(OrderedProduct orderedProduct : order.getOrderedProducts()) {
                OrderedProductDTO orderedProductDTO = new OrderedProductDTO();
                ProductDTO productDTO= new ProductDTO();
                productDTO.setProductId(orderedProduct.getProductId());
                orderedProductDTO.setOrderedProductId(orderedProduct.getOrderedProductId());
                orderedProductDTO.setQuantity(orderedProduct.getQuantity());
                orderedProductDTO.setProduct(productDTO);
                orderedProductDTOs.add(orderedProductDTO);
            }
            orderDTO.setOrderedProducts(orderedProductDTOs);
            orderDTO.setDeliveryAddress(order.getDeliveryAddress());
            orderDTOs.add(orderDTO);
        }
        return orderDTOs;
    }
}
