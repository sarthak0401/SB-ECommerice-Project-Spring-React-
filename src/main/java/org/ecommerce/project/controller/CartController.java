package org.ecommerce.project.controller;

import org.ecommerce.project.model.Cart;
import org.ecommerce.project.payload.CartDTO;
import org.ecommerce.project.repositories.CartRepository;
import org.ecommerce.project.service.CartService;
import org.ecommerce.project.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@EnableMethodSecurity
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private AuthUtils authUtils;
    @Autowired
    private CartRepository cartRepository;

    // Adding the products to the cart
        @PostMapping("/cart/products/{productId}/quantity/{quantity}")
        public ResponseEntity<CartDTO> addProductToCart (@PathVariable Long productId, @PathVariable Integer quantity){
            CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
        }


        // API to get all the carts -> so this will help to know all the carts and if there are a lot of high value customer's cart (maybe the frequent heavy buyers, or lets say there are a lot of cart items in it, cart total for them is very large but not checked out yet) We can give them coupons and offers to make them checkout the things, that will boost our revenue
    // This api is for the vendor (seller)

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts(){
            List<CartDTO> allCarts = cartService.getAllCarts();
            return new ResponseEntity<>(allCarts, HttpStatus.FOUND);
    }


    // Fetching the cart of a specific user by the ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cart/user/{userID}")
    public ResponseEntity<CartDTO> getUserCart(@PathVariable Long userID){
            CartDTO cartDTO = cartService.getUserCartById(userID);
            return new ResponseEntity<>(cartDTO, HttpStatus.FOUND);
    }


    // This API we are creating for the user to fetch their own cart, so we will fetch the user based on the email of the authenticated User (currently logged in) and here we will have cartId as well, cartId will be used for future concept, like if we want the user to have multiple carts, lets say different on phone, different on pc, we could use this api to fetch a particular cart. Right now we have 1:1 mapping, one user one cart
    @GetMapping("/carts/user/cart")
    public ResponseEntity<CartDTO> getCartByID(){
            String emailId = authUtils.loggedInUserEmail();
            Cart cart = cartRepository.findCartByUser_Email(emailId);
            Long cartId = cart.getCartId();

            CartDTO cartDTO = cartService.getCart(emailId, cartId);
            return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    // This method will help to delete, increase the quantity of decrease the quantity
    // - 1 +, this way we have the product in the frontend, with - button click we will run delete operation, and with + button it will run the add operation.
    // And the logic will be written, such that when the quantity reaches 0, it will be removed from the cart
    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(
            @PathVariable Long productId,
            @PathVariable String operation
    ){
            CartDTO cartDTO = cartService.updateProductQuantityInCart(productId, operation.equalsIgnoreCase("delete") ? -1 : 1 );

            return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);}


    @DeleteMapping("/cart/{cartId}/products/{productId}")
    public ResponseEntity<String> deleteProductFromCart(
            @PathVariable Long cartId,
            @PathVariable Long productId
    ){
        String message = cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

}
