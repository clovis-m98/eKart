package com.ekart.payment.repository;

import com.ekart.payment.entity.Card;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends CrudRepository<Card, Integer> {
    List<Card> findByCustomerEmailId(String customerEmailId);
    List<Card> findByCustomerEmailIdAndCardType(String customerEmailId,String cardType);
}
