package com.ekart.product.service;

import com.ekart.product.dto.ProductDTO;

import java.util.List;

public interface CustomerProductService {
    List<ProductDTO> getAllProducts() throws Exception;
    ProductDTO getProductById(Integer productId) throws Exception;
    void reduceAvailableQuantity(Integer productId, Integer quantity) throws Exception ;
}
