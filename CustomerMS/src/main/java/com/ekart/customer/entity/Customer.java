package com.ekart.customer.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EK_CUSTOMER")
@Data
public class Customer {
    @Id
    private String emailId;
    private String name;
    private String password;
    private String phoneNumber;
    private String address;
}
