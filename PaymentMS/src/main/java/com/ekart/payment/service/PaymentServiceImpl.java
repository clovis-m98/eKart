package com.ekart.payment.service;

import com.ekart.payment.dto.CardDTO;
import com.ekart.payment.dto.TransactionDTO;
import com.ekart.payment.dto.TransactionStatus;
import com.ekart.payment.entity.Card;
import com.ekart.payment.entity.Transaction;
import com.ekart.payment.repository.CardRepository;
import com.ekart.payment.repository.TransactionRepository;
import com.ekart.payment.utility.HashingUtility;
import com.ekart.payment.utility.PayOrderFallbackException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service(value="paymentService")
@Transactional
public class PaymentServiceImpl implements PaymentService{

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    Environment environment;
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Integer addCustomerCard(String customerEmailId, CardDTO cardDTO)
            throws Exception, NoSuchAlgorithmException {

        List<Card> listOfCustomerCards = cardRepository.findByCustomerEmailId(customerEmailId);
        if(listOfCustomerCards.isEmpty())
            throw new Exception(environment.getProperty("PaymentService.CUSTOMER_NOT_FOUND"));
        cardDTO.setHashCvv(HashingUtility.getHashValue(cardDTO.getCvv().toString()));
        Card newCard = new Card();
        newCard.setCardId(cardDTO.getCardId());
        newCard.setNameOnCard(cardDTO.getNameOnCard());;
        newCard.setCardNumber(cardDTO.getCardNumber());
        newCard.setCardType(cardDTO.getCardType());
        //newCard.setExpiryDate(cardDTO.getExpiryMonth()+"-"+cardDTO.getExpiryYear());
        newCard.setExpiryDate(cardDTO.getExpiryDate());
        newCard.setCvv(cardDTO.getHashCvv());
        newCard.setCustomerEmailId(cardDTO.getCustomerEmailId());;

        cardRepository.save(newCard);
        return newCard.getCardId();
    }

    @Override
    public void updateCustomerCard(CardDTO cardDTO) throws Exception, NoSuchAlgorithmException {


        Optional<Card> optionalCard = cardRepository.findById(cardDTO.getCardId());
        Card card = optionalCard.orElseThrow(() ->
                new Exception(environment.getProperty("PaymentService.CARD_NOT_FOUND")));
        cardDTO.setHashCvv(HashingUtility.getHashValue(cardDTO.getCvv().toString()));
        card.setCardId(cardDTO.getCardId());
        card.setNameOnCard(cardDTO.getNameOnCard());
        card.setCardNumber(cardDTO.getCardNumber());
        card.setCardType(cardDTO.getCardType());
        card.setCvv(cardDTO.getHashCvv());
        //card.setCvv(cardDTO.getCvv());
        card.setExpiryDate(cardDTO.getExpiryDate());
        //card.setExpiryDate(cardDTO.getExpiryDate());
        card.setCustomerEmailId(cardDTO.getCustomerEmailId());

    }

    @Override
    public void deleteCustomerCard(String customerEmailId, Integer cardId) throws Exception {

        List<Card> listOfCustomerCards = cardRepository.findByCustomerEmailId(customerEmailId);
        if(listOfCustomerCards.isEmpty())
            throw new Exception(environment.getProperty("PaymentService.CUSTOMER_NOT_FOUND"));

        Optional<Card> optionalCards = cardRepository.findById(cardId);
        Card card = optionalCards.orElseThrow(() ->
                new Exception(environment.getProperty("PaymentService.CARD_NOT_FOUND")));
        cardRepository.delete(card);

    }

    @Override
    public  CardDTO getCard(Integer cardId) throws Exception{

        Optional<Card> optionalCards = cardRepository.findById(cardId);
        Card card = optionalCards.orElseThrow(() ->
                new Exception(environment.getProperty("PaymentService.CARD_NOT_FOUND")));
        CardDTO cardDTO = new CardDTO();
        cardDTO.setCardId(card.getCardId());
        cardDTO.setNameOnCard(card.getNameOnCard());
        cardDTO.setCardNumber(card.getCardNumber());
        cardDTO.setCardType(card.getCardType());
        cardDTO.setHashCvv(card.getCvv());
        //String[] expiryDate = card.getExpiryDate().split("-");
        cardDTO.setExpiryDate(card.getExpiryDate());
        //cardDTO.setExpiryYear(expiryDate[0]);
        cardDTO.setCustomerEmailId(card.getCustomerEmailId());
        return cardDTO;
    }

    @Override
    public List<CardDTO> getCustomerCardOfCardType(String customerEmailId, String cardType)
            throws Exception {
        List<Card> cards = cardRepository.findByCustomerEmailIdAndCardType(customerEmailId, cardType);
        if(cards.isEmpty())
            throw new Exception(environment.getProperty("PaymentService.CARD_NOT_FOUND"));
        List<CardDTO> cardDTOs = new ArrayList<CardDTO>();
        for(Card card : cards) {
            CardDTO cardDTO = new CardDTO();
            cardDTO.setCardId(card.getCardId());
            cardDTO.setNameOnCard(card.getNameOnCard());
            cardDTO.setCardNumber(card.getCardNumber());
            cardDTO.setCardType(card.getCardType());
            cardDTO.setHashCvv("XXX");
            //String[] expiryDate = card.getExpiryDate().split("-");
            cardDTO.setExpiryDate(card.getExpiryDate());
            cardDTO.setCustomerEmailId(card.getCustomerEmailId());
            cardDTOs.add(cardDTO);
        }
        return cardDTOs;
    }

    @Override
    public Integer addTransaction(TransactionDTO transactionDTO)
            throws Exception, PayOrderFallbackException {
        if(transactionDTO.getTransactionStatus().equals(TransactionStatus.TRANSACTION_FAILED))
            throw new PayOrderFallbackException(environment
                    .getProperty("Payment.TRANSACTION_FAILED_CVV_NOT_MATCHING"));
        Transaction transaction = new Transaction();
        transaction.setCardId(transactionDTO.getCard().getCardId());
        transaction.setOrderId(transactionDTO.getOrder().getOrderId());
        transaction.setTotalPrice(transactionDTO.getTotalPrice());
        transaction.setTransactionDate(transactionDTO.getTransactionDate());
        transaction.setTransactionStatus(transactionDTO.getTransactionStatus());
        transactionRepository.save(transaction);
        return transaction.getTransactionId();
    }

    @Override
    public TransactionDTO authenticatePayment(String customerEmailId, TransactionDTO transactionDTO)
            throws Exception, NoSuchAlgorithmException {
        if(! transactionDTO.getOrder().getCustomerEmailId().equals(customerEmailId))
            throw new Exception(environment
                    .getProperty("PaymentService.ORDER_DOES_NOT_BELONGS"));
        if(! transactionDTO.getOrder().getOrderStatus().equals("PLACED"))
            throw new Exception(environment
                    .getProperty("PaymentService.TRANSACTION_ALREADY_DONE"));
        CardDTO cardDTO = getCard(transactionDTO.getCard().getCardId());
        if(! cardDTO.getCustomerEmailId().matches(customerEmailId))
            throw new Exception(environment
                    .getProperty("PaymentService.CARD_DOES_NOT_BELONGS"));
        if(! cardDTO.getCardType().equals(transactionDTO.getOrder().getPaymentThrough()))
            throw new Exception(environment
                    .getProperty("PaymentService.PAYMENT_OPTION_SELECTED_NOT_MATCHING_CARD_TYPE"));
        if(cardDTO.getHashCvv().equals(HashingUtility.getHashValue(transactionDTO.getCard().getCvv().toString())))
            transactionDTO.setTransactionStatus(TransactionStatus.TRANSACTION_SUCCESS);
        else transactionDTO.setTransactionStatus(TransactionStatus.TRANSACTION_FAILED);
        return transactionDTO;
    }
}