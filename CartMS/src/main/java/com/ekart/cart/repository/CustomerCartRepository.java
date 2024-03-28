package com.ekart.cart.repository;

import com.ekart.cart.entity.CustomerCart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerCartRepository extends CrudRepository<CustomerCart, Integer> {

    Optional<CustomerCart> findByCustomerEmailId(String customerEmailId);

}