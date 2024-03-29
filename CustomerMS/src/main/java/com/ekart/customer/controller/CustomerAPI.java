package com.ekart.customer.controller;

import com.ekart.customer.dto.CartProductDTO;
import com.ekart.customer.dto.CustomerCartDTO;
import com.ekart.customer.dto.CustomerDTO;
import com.ekart.customer.dto.ProductDTO;
import com.ekart.customer.service.CustomerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;

@RequestMapping(value = "/customer-api")
@RestController
@Validated
@CrossOrigin
public class CustomerAPI {

    @Autowired
    private CustomerService customerService;

    @Autowired
    RestTemplate template;

    @Autowired
    private Environment environment;

    static Log logger = LogFactory.getLog(CustomerAPI.class);



    @PostMapping(value = "/login")
    public ResponseEntity<CustomerDTO> authenticateCustomer(@Valid @RequestBody CustomerDTO customerDTO)
            throws Exception {

        logger.info("CUSTOMER TRYING TO LOGIN, VALIDATING CREDENTIALS. CUSTOMER EMAIL ID: " + customerDTO.getEmailId());
        CustomerDTO customerDTOFromDB = customerService.authenticateCustomer(customerDTO.getEmailId(),
                customerDTO.getPassword());
        logger.info("CUSTOMER LOGIN SUCCESS, CUSTOMER EMAIL : " + customerDTOFromDB.getEmailId());
        return new ResponseEntity<>(customerDTOFromDB, HttpStatus.OK);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<String> registerCustomer(@Valid @RequestBody CustomerDTO customerDTO)
            throws Exception {

        logger.info("CUSTOMER TRYING TO REGISTER. CUSTOMER EMAIL ID: " + customerDTO.getEmailId());
        String registeredWithEmailID = customerService.registerNewCustomer(customerDTO);
        registeredWithEmailID = environment.getProperty("CustomerAPI.CUSTOMER_REGISTRATION_SUCCESS")
                + registeredWithEmailID;
        return new ResponseEntity<>(registeredWithEmailID, HttpStatus.OK);
    }

    @PutMapping(value = "/customer/{customerEmailId}/address/")
    public ResponseEntity<String> updateShippingAddress(
            @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",
                    message = "{invalid.email.format}")
            @PathVariable String customerEmailId,
            @RequestBody String address) throws Exception {

        customerService.updateShippingAddress(customerEmailId, address);
        String modificationSuccessMsg = environment.getProperty("CustomerAPI.UPDATE_ADDRESS_SUCCESS");
        return new ResponseEntity<>(modificationSuccessMsg, HttpStatus.OK);

    }

    @DeleteMapping(value = "/customer/{customerEmailId}")
    public ResponseEntity<String> deleteShippingAddress(
            @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",
                    message = "{invalid.email.format}")
            @PathVariable("customerEmailId") String customerEmailId)
            throws Exception {

        customerService.deleteShippingAddress(customerEmailId);
        String modificationSuccessMsg = environment.getProperty("CustomerAPI.CUSTOMER_ADDRESS_DELETED_SUCCESS");
        return new ResponseEntity<>(modificationSuccessMsg, HttpStatus.OK);

    }

    @PostMapping(value = "/customercarts/add-product")
    public ResponseEntity<String> addProductToCart(@Valid @RequestBody CustomerCartDTO customerCartDTO)
            throws Exception {

        customerService.getCustomerByEmailId(customerCartDTO.getCustomerEmailId());
        Set<CartProductDTO> cartProductDTOS = new HashSet<>();
        for (CartProductDTO cartProductDTO : customerCartDTO.getCartProducts()) {

            ProductDTO productDTO = template.getForEntity("http://PRODUCTMS" + "/product-api/product/"
                    +cartProductDTO.getProduct().getProductId(), ProductDTO.class).getBody();
//            cartProductDTO.setProduct(productDTO);
            // We are calling the product API using hard-coded URI
            // Replace this call with the appropriate MS name
            // Product API is upscaled (available in 2 numbers). Hence, use load balanced
            // template to make call to the Product API
        }
        // We are calling the Cart API using hard-coded URI
        // Replace this call with the appropriate MS name
        // CartMS is not an upscaled one (available in 1 number) , still load-balanced
        // rest template should be used here to make the call
        // Don't create and autowire a normal rest template because load balanced
        // template is already in config file
//        customerCartDTO.setCartProducts(cartProductDTOS);
        return template.postForEntity("http://CARTMS" + "/cart-api/products",
                customerCartDTO, String.class);
    }

}
