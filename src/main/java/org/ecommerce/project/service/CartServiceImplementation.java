package org.ecommerce.project.service;

import org.ecommerce.project.exceptions.APIException;
import org.ecommerce.project.exceptions.ResourceNotFoundException;
import org.ecommerce.project.model.Cart;
import org.ecommerce.project.model.CartItem;
import org.ecommerce.project.model.Product;
import org.ecommerce.project.payload.CartDTO;
import org.ecommerce.project.payload.ProductDTO;
import org.ecommerce.project.repositories.CartItemRepository;
import org.ecommerce.project.repositories.CartRepository;
import org.ecommerce.project.repositories.ProductRepository;
import org.ecommerce.project.security.services.AuthUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImplementation implements CartService{

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AuthUtils authUtils;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    private Cart getOrCreateCart(){
        Cart cart = cartRepository.findCartByUser_Email(authUtils.loggedInUserEmail());
        // So AuthUtils is basically the class to get the information related to the authenticated user (currently logged in user)

        if(cart!=null) return cart;
        else{
            Cart newCart = new Cart();
            newCart.setTotalPrice(0.0);
            newCart.setUser(authUtils.getLoggedInUser());
            Cart createdCart =  cartRepository.save(newCart);
            return createdCart;
        }
    }


    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        // We need to find the existing cart or create one
        Cart cart = getOrCreateCart();


        // Retrieving the product details from the database from the id
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productID", productId));

        // Checking if this item already exist in the cart
        CartItem cartItem = cartItemRepository.findCartItemsByCart_CartIdAndProduct_ProductId(cart.getCartId(), productId);
        // We are passing the cart-id to check cart for specific user only, as there will be many carts for the different users


        // Performing validation -> Check if the stock exits or not, etc
        if(cartItem!=null){
            throw new APIException("Product " + product.getProductName() + " already exists in the cart!" );
        }

        if(product.getQuantity()==0) {
            throw new APIException(product.getProductName() + " is Not available");
        }

        if(product.getQuantity() < quantity){
            throw new APIException("Please make an order of the " + product.getProductName() + " less than or equal to quantity : " + product.getQuantity());

        }
        // Create Cart Item

        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());


        // Save Cart Item
        cartItemRepository.save(newCartItem);

        // product.setQuantity(product.getQuantity() - quantity);  // This will reduce the stock of the product when the item is added to the cart
        // Or we can reduce the stock when the order is placed

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);

        // Return updated cart
        CartDTO cartDTO =  modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }


}
