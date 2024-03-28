package com.ekart.cart.service;

import com.ekart.cart.dto.CartProductDTO;
import com.ekart.cart.dto.CustomerCartDTO;

import java.util.Set;

public interface CustomerCartService {
    Integer addProductToCart(CustomerCartDTO customerCart) throws Exception;
    Set<CartProductDTO> getProductsFromCart(String customerEmailId) throws Exception;
    void modifyQuantityOfProductInCart(String customerEmailId, Integer productId ,Integer quantity)
            throws Exception;
    void deleteProductFromCart(String customerEmailId,Integer productId) throws Exception;
    void deleteAllProductsFromCart(String customerEmailId) throws Exception;
}
