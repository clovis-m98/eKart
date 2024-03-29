package com.ekart.payment.repository;

import com.ekart.payment.entity.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository   extends CrudRepository<Transaction, Integer> {
}
