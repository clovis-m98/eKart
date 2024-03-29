package com.ekart.customer.repository;

import java.util.List;

import com.ekart.customer.entity.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {
    List<Order> findByCustomerEmailId(String customerEmailId);
}
