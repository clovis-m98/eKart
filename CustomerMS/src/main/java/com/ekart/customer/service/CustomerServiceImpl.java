package com.ekart.customer.service;

import com.ekart.customer.dto.CustomerDTO;
import com.ekart.customer.entity.Customer;
import com.ekart.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service(value = "customerService")
@Transactional
public class CustomerServiceImpl implements CustomerService{

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    Environment environment;

    // This method will authenticate customer email id and password and return customer details
    @Override
    public CustomerDTO authenticateCustomer(String emailId, String password) throws Exception {
        CustomerDTO customerDTO = null;

        //retrieving customer data from repository
        Optional<Customer> optionalCustomer = customerRepository.findById(emailId.toLowerCase());
        Customer customer = optionalCustomer.orElseThrow(() ->
                new Exception(environment.getProperty("CustomerService.CUSTOMER_NOT_FOUND")));
        //comparing entered password with password stored in DB
        if (!password.equals(customer.getPassword()))
            throw new Exception(environment.getProperty("CustomerService.INVALID_CREDENTIALS"));
        customerDTO = new CustomerDTO();
        customerDTO.setEmailId(customer.getEmailId());
        customerDTO.setName(customer.getName());
        customerDTO.setPhoneNumber(customer.getPhoneNumber());
        customerDTO.setAddress(customer.getAddress());
        return customerDTO;
    }

    //This method will add a new customer
    @Override
    public String registerNewCustomer(CustomerDTO customerDTO) throws Exception {
        String registeredWithEmailId = null;
        //check whether specified email id is already in use by other customer
        boolean isEmailNotAvailable = customerRepository.findById(customerDTO.getEmailId().toLowerCase()).isEmpty();
        //check whether specified phone no. is already in use by other customer
        boolean isPhoneNumberNotAvailable = customerRepository.findByPhoneNumber(customerDTO.getPhoneNumber()).isEmpty();
        if (isEmailNotAvailable) {
            if (isPhoneNumberNotAvailable) {
                Customer customer = new Customer();
                customer.setEmailId(customerDTO.getEmailId().toLowerCase());
                customer.setName(customerDTO.getName());
                customer.setPassword(customerDTO.getPassword());
                customer.setPhoneNumber(customerDTO.getPhoneNumber());
                customer.setAddress(customerDTO.getAddress());
                customerRepository.save(customer);
                registeredWithEmailId = customer.getEmailId();
            } else {
                throw new Exception(environment.getProperty("CustomerService.PHONE_NUMBER_ALREADY_IN_USE"));
            }
        } else {
            throw new Exception(environment.getProperty("CustomerService.EMAIL_ID_ALREADY_IN_USE"));
        }
        return registeredWithEmailId;

    }

    // This method will update name and phone number of a customer
    //this method will update the password of customer
    //this method will update the address
    @Override
    public void updateShippingAddress(String customerId , String address) throws Exception {
        //retrieving address details from repository
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId.toLowerCase());
        Customer customer = optionalCustomer.orElseThrow(() ->
                new Exception(environment.getProperty("CustomerService.CUSTOMER_NOT_FOUND")));
        customer.setAddress(address);
    }

    //this method will remove address from a particular customer details
    @Override
    public void deleteShippingAddress(String customerEmailId) throws Exception {
        //retrieving customer details from repository
        Optional<Customer> optionalCustomer = customerRepository.findById(customerEmailId.toLowerCase());
        Customer customer = optionalCustomer.orElseThrow(() ->
                new Exception(environment.getProperty("CustomerService.CUSTOMER_NOT_FOUND")));
        customer.setAddress(null);
    }

    @Override
    public CustomerDTO getCustomerByEmailId(String emailId) throws Exception {
        CustomerDTO customerDTO = null;
        //retrieving customer data from repository
        Optional<Customer> optionalCustomer = customerRepository.findById(emailId.toLowerCase());
        Customer customer = optionalCustomer.orElseThrow(() ->
                new Exception(environment.getProperty("CustomerService.CUSTOMER_NOT_FOUND")));
        //comparing entered password with password stored in DB
        customerDTO = new CustomerDTO();
        customerDTO.setEmailId(customer.getEmailId());
        customerDTO.setName(customer.getName());
        customerDTO.setPhoneNumber(customer.getPhoneNumber());
        customerDTO.setAddress(customer.getAddress());
        return customerDTO;

    }
}
