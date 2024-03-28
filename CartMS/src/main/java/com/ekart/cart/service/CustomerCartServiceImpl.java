package com.ekart.cart.service;

import com.ekart.cart.dto.CartProductDTO;
import com.ekart.cart.dto.CustomerCartDTO;
import com.ekart.cart.dto.ProductDTO;
import com.ekart.cart.entity.CartProduct;
import com.ekart.cart.entity.CustomerCart;
import com.ekart.cart.repository.CartProductRepository;
import com.ekart.cart.repository.CustomerCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(value = "customerCartService")
@Transactional
public class CustomerCartServiceImpl implements CustomerCartService {
    @Autowired
    Environment environment;
    @Autowired
    private CustomerCartRepository customerCartRepository;

    @Autowired
    private CartProductRepository cartProductRepository;

    // this method adds new products in to the customer cart
    @Override
    public Integer addProductToCart(CustomerCartDTO customerCartDTO) throws Exception {
        Set<CartProduct> cartProducts= new HashSet<>();
        Integer cartId = null;
        for(CartProductDTO cartProductDTO : customerCartDTO.getCartProducts())
        {
            CartProduct cartProduct = new CartProduct();
            cartProduct.setProductId(cartProductDTO.getProduct().getProductId());
            cartProduct.setQuantity(cartProductDTO.getQuantity());
            cartProducts.add(cartProduct);
        }
        Optional<CustomerCart> cartOptional = customerCartRepository
                .findByCustomerEmailId(customerCartDTO.getCustomerEmailId());
        if(cartOptional.isEmpty()) {
            CustomerCart newCart =new  CustomerCart();
            newCart.setCustomerEmailId(customerCartDTO.getCustomerEmailId());
            newCart.setCartProducts(cartProducts);
            customerCartRepository.save(newCart);
            cartId = newCart.getCartId();
        }
        else {
            CustomerCart cart =cartOptional.get();
            for(CartProduct cartProductToBeAdded: cartProducts) {
                boolean checkProductAlreadyPresent =false;
                for(CartProduct cartProductFromCart: cart.getCartProducts()) {
                    if(cartProductFromCart.equals(cartProductToBeAdded)) {
                        cartProductFromCart.setQuantity(cartProductToBeAdded.getQuantity()
                                + cartProductFromCart.getQuantity());
                    checkProductAlreadyPresent=true;
                    }
                }
                if(checkProductAlreadyPresent == false) {
                    cart.getCartProducts().add(cartProductToBeAdded);
                }
            }
            cartId = cart.getCartId();
        }
        return cartId;
    }

    // this method retrieves customer data from repository and returns cart details
    // of that customer
    @Override
    public Set<CartProductDTO> getProductsFromCart(String customerEmailId) throws Exception {
        Optional<CustomerCart> cartOptional = customerCartRepository
                .findByCustomerEmailId(customerEmailId);
        Set<CartProductDTO> cartProductsDTO = new HashSet<>();
        CustomerCart cart = cartOptional.orElseThrow(() ->
                new Exception(environment.getProperty("CustomerCartService.NO_CART_FOUND")));
        if (cart.getCartProducts().isEmpty()) {
            throw new Exception(environment.getProperty("CustomerCartService.NO_PRODUCT_ADDED_TO_CART"));
        }
        Set<CartProduct> cartProducts = cart.getCartProducts();
        for (CartProduct cartProduct : cartProducts) {
            CartProductDTO cartProductDTO = new CartProductDTO();
            cartProductDTO.setCartProductId(cartProduct.getCartProductId());
            cartProductDTO.setQuantity(cartProduct.getQuantity());
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(cartProduct.getProductId());
            cartProductDTO.setProduct(productDTO);
            cartProductsDTO.add(cartProductDTO);
        }
        return cartProductsDTO;
    }
    // this method retrieves customer data from repository and returns cart details
    // of that customer
    @Override
    public void deleteProductFromCart(String customerEmailId, Integer productId) throws Exception {
        Optional<CustomerCart> cartOptional = customerCartRepository
                .findByCustomerEmailId(customerEmailId);
        CustomerCart cart = cartOptional.orElseThrow(() ->
                new Exception(environment.getProperty("CustomerCartService.NO_CART_FOUND")));
        if (cart.getCartProducts().isEmpty()) {
            throw new Exception(environment.getProperty("CustomerCartService.NO_PRODUCT_ADDED_TO_CART"));
        }
        CartProduct selectedProduct = null;
        for (CartProduct product : cart.getCartProducts()) {
            if (product.getProductId().equals(productId)) {
                selectedProduct = product;
            }
        }
        if (selectedProduct == null) {
            throw new Exception(environment.getProperty("CustomerCartService.PRODUCT_ALREADY_NOT_AVAILABLE"));
        }
        cart.getCartProducts().remove(selectedProduct);
        cartProductRepository.delete(selectedProduct);
    }

    @Override
    public void deleteAllProductsFromCart(String customerEmailId) throws Exception {
        Optional<CustomerCart> cartOptional = customerCartRepository
                .findByCustomerEmailId(customerEmailId);
        CustomerCart cart = cartOptional.orElseThrow(() ->
                new Exception(environment.getProperty("CustomerCartService.NO_CART_FOUND")));

        if (cart.getCartProducts().isEmpty()) {
            throw new Exception(environment.getProperty("CustomerCartService.NO_PRODUCT_ADDED_TO_CART"));
        }
        List<Integer> productIds = new ArrayList<>();
        cart.getCartProducts().parallelStream().forEach(cp -> {
            productIds.add(cp.getCartProductId());
            cart.getCartProducts().remove(cp);
        });
        productIds.forEach(pid -> {
            cartProductRepository.deleteById(pid);
        });

    }

    @Override
    public void modifyQuantityOfProductInCart(String customerEmailId, Integer productId, Integer quantity)
            throws Exception {

        Optional<CustomerCart> cartOptional = customerCartRepository
                .findByCustomerEmailId(customerEmailId);
        CustomerCart cart = cartOptional.orElseThrow(() ->
                new Exception(environment.getProperty("CustomerCartService.NO_CART_FOUND")));

        if (cart.getCartProducts().isEmpty()) {
            throw new Exception(environment.getProperty("CustomerCartService.NO_PRODUCT_ADDED_TO_CART"));
        }
        CartProduct selectedProduct = null;
        for (CartProduct product : cart.getCartProducts()) {
            if (product.getProductId().equals(productId)) {
                selectedProduct = product;
            }
        }
        if (selectedProduct == null) {
            throw new Exception(environment.getProperty("CustomerCartService.PRODUCT_ALREADY_NOT_AVAILABLE"));
        }
        selectedProduct.setQuantity(quantity);
    }

}
