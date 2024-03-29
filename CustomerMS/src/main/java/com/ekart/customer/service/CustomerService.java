package com.ekart.customer.service;

import com.ekart.customer.dto.CustomerDTO;

public interface CustomerService {
    CustomerDTO authenticateCustomer(String emailId, String password) throws Exception;
    String registerNewCustomer(CustomerDTO customerDTO) throws Exception;

//	void updateProfile(CustomerDTO customerDTO) throws Exception;
//	void changePassword(String customerEmailId, String currentPassword, String newPassword)
//	throws Exception;

    void updateShippingAddress(String customerEmailId , String address) throws Exception;
    void deleteShippingAddress(String customerEmailId) throws Exception;
    CustomerDTO getCustomerByEmailId(String emailId) throws Exception;
}
