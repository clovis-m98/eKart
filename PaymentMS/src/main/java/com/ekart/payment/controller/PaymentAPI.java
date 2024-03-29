package com.ekart.payment.controller;

import com.ekart.payment.dto.CardDTO;
import com.ekart.payment.dto.OrderDTO;
import com.ekart.payment.dto.TransactionDTO;
import com.ekart.payment.dto.TransactionStatus;
import com.ekart.payment.service.PaymentCircuitBreakerService;
import com.ekart.payment.service.PaymentService;
import com.ekart.payment.utility.PayOrderFallbackException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RequestMapping(value = "/payment-api")
@RestController
@CrossOrigin
@Validated
@EnableAutoConfiguration
@CircuitBreaker(name = "payForOrder")
public class PaymentAPI {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private Environment environment;

    @Autowired
    private RestTemplate template;

    private static final Log LOGGER = LogFactory.getLog(PaymentAPI.class);

    @Autowired
    private PaymentCircuitBreakerService paymentCircuitBreakerService;

    @PostMapping(value = "/customer/{customerEmailId}/cards")
    public ResponseEntity<String> addNewCard(@RequestBody CardDTO cardDTO,
                                             @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",
                                                     message = "{invalid.email.format}")
                                             @PathVariable("customerEmailId") String customerEmailId)
            throws Exception, NoSuchAlgorithmException {
        LOGGER.info("Received request to add new  card for customer : " + cardDTO.getCustomerEmailId());

        int cardId;
        cardId = paymentService.addCustomerCard(customerEmailId, cardDTO);
        String message = environment.getProperty("PaymentAPI.NEW_CARD_ADDED_SUCCESS");
        String toReturn = message + cardId;
        toReturn = toReturn.trim();
        return new ResponseEntity<>(toReturn, HttpStatus.OK);

    }

    @PutMapping(value = "/update/card")
    public ResponseEntity<String> updateCustomerCard(@Valid @RequestBody CardDTO cardDTO)
            throws Exception, NoSuchAlgorithmException {
        LOGGER.info("Received request to update  card :" + cardDTO.getCardId() + " of customer : "
                + cardDTO.getCustomerEmailId());

        paymentService.updateCustomerCard(cardDTO);
        String modificationSuccessMsg = environment.getProperty("PaymentAPI.UPDATE_CARD_SUCCESS");
        return new ResponseEntity<>(modificationSuccessMsg, HttpStatus.OK);

    }

    @DeleteMapping(value = "/customer/{customerEmailId}/card/{cardID}/delete")
    public ResponseEntity<String> deleteCustomerCard(@PathVariable("cardID") Integer cardID,
                                                     @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",
                                                             message = "{invalid.email.format}")
                                                     @PathVariable("customerEmailId") String customerEmailId)
            throws Exception {
        LOGGER.info("Received request to delete  card :" + cardID + " of customer : " + customerEmailId);

        paymentService.deleteCustomerCard(customerEmailId, cardID);
        String modificationSuccessMsg = environment.getProperty("PaymentAPI.CUSTOMER_CARD_DELETED_SUCCESS");
        return new ResponseEntity<>(modificationSuccessMsg, HttpStatus.OK);

    }

    // Get the customer cards details by calling getCardsOfCustomer()
    // method of PaymentService() and return the list of card details obtained
    @GetMapping(value = "/customer/{customerEmailId}/card-type/{cardType}")
    public ResponseEntity<List<CardDTO>> getCardsOfCustomer(@PathVariable String customerEmailId,
                                                            @PathVariable String cardType)
            throws Exception {
        // Write your logic here
        List<CardDTO> cardDTOS = paymentService.getCustomerCardOfCardType(customerEmailId, cardType);
        return new ResponseEntity<>(cardDTOS, HttpStatus.OK);
    }




    // Annotate this method for handling resilience
    // Get the order details from CustomerMS for the given orderId (available in
    // TransactionDTO)
    // Update the Transaction details with the obtained Order details in above step,
    // along with transaction date and total price
    // Authenticate the transaction details for the given customer by calling
    // authenticatePayment() method of PaymentService
    // Add the transaction details to the database by calling addTransaction()
    // method of PaymentService
    // Update the order status by calling updateOrderAfterPayment() method of
    // PaymentCircuitBreakerService
    // Set the appropriate success or failure message and return the same
    @CircuitBreaker(name = "payForOrder", fallbackMethod = "payForOrderFallback")
    @PostMapping(value = "/customer/{customerEmailId}/pay-order")
    public ResponseEntity<String> payForOrder(
            @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",
                    message = "{invalid.email.format}")
            @PathVariable String customerEmailId,
            @Valid @RequestBody TransactionDTO transactionDTO)
            throws NoSuchAlgorithmException, Exception, PayOrderFallbackException {
        // Write your logic here
        OrderDTO orderDTO =
                template.getForEntity("http://CUSTOMERMS" + "/customerorder-api/order/"
                + transactionDTO.getOrder().getOrderId(), OrderDTO.class).getBody();
        assert orderDTO != null;
        transactionDTO.setTransactionDate(orderDTO.getDateOfOrder());
        transactionDTO.setTotalPrice(orderDTO.getTotalPrice());
        transactionDTO.setOrder(orderDTO);
        TransactionDTO transaction = paymentService.authenticatePayment(customerEmailId,transactionDTO);
        int id = paymentService.addTransaction(transaction);
        paymentCircuitBreakerService.updateOrderAfterPayment(transaction.getOrder().getOrderId(),
                transaction.getTransactionStatus().toString());
        String message = "Order Placed Successfully with id: " + id;
        return new ResponseEntity<>(message, HttpStatus.OK);
    }//TODO:complete this if Remaining

    // Implement a fallback method here
    // If exception message is Payment.TRANSACTION_FAILED_CVV_NOT_MATCHING then set
    // message as Payment.TRANSACTION_FAILED_CVV_NOT_MATCHING
    // Else set the message as PaymentAPI.PAYMENT_FAILURE_FALLBACK
    // Return the above message as response

    public ResponseEntity<String> payForOrderFallback(String customerEmailId, TransactionDTO transactionDTO,
                                                      RuntimeException exception) {
        //Write your logic here
        String message = null;
        if(exception.getMessage().equals(environment.getProperty("Payment.TRANSACTION_FAILED_CVV_NOT_MATCHING")))
            message = environment.getProperty("Payment.TRANSACTION_FAILED_CVV_NOT_MATCHING");
        message = environment.getProperty("PaymentAPI.PAYMENT_FAILURE_FALLBACK");
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}