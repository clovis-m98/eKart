package com.ekart.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableDiscoveryClient
@PropertySource("classpath:messages.properties")
@PropertySource("classpath:ValidationMessages.properties")
public class CartMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartMsApplication.class, args);
	}

}
