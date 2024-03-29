package com.ekart.product.controller;

import com.ekart.product.dto.ProductDTO;
import com.ekart.product.service.CustomerProductService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/product-api")
@RestController
@CrossOrigin
@Validated
public class CustomerProductAPI {

    @Autowired
    private CustomerProductService customerProductService;

    @Autowired
    private Environment environment;


    Log logger = LogFactory.getLog(CustomerProductAPI.class);

    // Get all the product details by calling getAllProducts() of
    // CustomerProductService and return the same
    @GetMapping(value = "/products")
    public ResponseEntity<List<ProductDTO>> getAllProducts() throws Exception {
        // Write your logic here
        List<ProductDTO> productDTOS = customerProductService.getAllProducts();
        return new ResponseEntity<>(productDTOS, HttpStatus.OK);
    }

    // Get the specific product by calling getProductById() of
    // CustomerProductService and return the same
    @GetMapping(value = "/product/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Integer productId) throws Exception {
        //Write your logic here
        System.out.println("==============================IN PRODUCT_MS==============================");
        ProductDTO productDTO = customerProductService.getProductById(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    // reduce the available quantity of product by calling reduceAvailableQuantity()
    // of CustomerProductService and return the message with property as
    // ProductAPI.REDUCE_QUANTITY_SUCCESSFULL
    @PutMapping(value = "/update/{productId}")
    public ResponseEntity<String> reduceAvailableQuantity(@PathVariable Integer productId ,
                                                          @RequestBody Integer quantity) throws Exception {
        //Write your logic here
        customerProductService.reduceAvailableQuantity(productId, quantity);
        String message = environment.getProperty("ProductAPI.REDUCE_QUANTITY_SUCCESSFULL");
        return new ResponseEntity<>(message, HttpStatus.OK);

    }
}
