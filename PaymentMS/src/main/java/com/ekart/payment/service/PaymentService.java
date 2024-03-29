package com.ekart.payment.service;

import com.ekart.payment.dto.CardDTO;
import com.ekart.payment.dto.TransactionDTO;
import com.ekart.payment.utility.PayOrderFallbackException;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface PaymentService {
    Integer addCustomerCard(String customerEmailId, CardDTO cardDTO)
            throws Exception, NoSuchAlgorithmException;
    void updateCustomerCard(CardDTO cardDTO) throws Exception, NoSuchAlgorithmException;
    void deleteCustomerCard(String customerEmailId, Integer cardId) throws Exception;
    CardDTO getCard(Integer cardId) throws Exception;
    List<CardDTO> getCustomerCardOfCardType(String customerEmailId , String cardType) throws Exception;
    Integer addTransaction (TransactionDTO transactionDTO) throws Exception, PayOrderFallbackException;
    TransactionDTO authenticatePayment(String customerEmailId , TransactionDTO transactionDTO)
            throws Exception , NoSuchAlgorithmException;
}
