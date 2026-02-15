package org.ecommerce.project.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.ecommerce.project.exceptions.APIException;
import org.ecommerce.project.exceptions.ResourceNotFoundException;
import org.ecommerce.project.model.Cart;
import org.ecommerce.project.model.CartItem;
import org.ecommerce.project.model.Product;
import org.ecommerce.project.payload.CartDTO;
import org.ecommerce.project.payload.CartItemDTO;
import org.ecommerce.project.payload.ProductDTO;
import org.ecommerce.project.repositories.CartItemRepository;
import org.ecommerce.project.repositories.CartRepository;
import org.ecommerce.project.repositories.ProductRepository;
import org.ecommerce.project.util.AuthUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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
        newCartItem.setCart(cart);  // Here we are mapping the cart to the newCartItem
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        // Adding the new product to the cart side, and above we have set the cart to the new product (newCartItem.setCart(cart);), we need to do the map both sides explicitly to maintain the bidirectional mapping of the entity
        cart.getCartItems().add(newCartItem);
        // So with above code we are mapping the newCartItem to the cart


        // Save Cart Item
        cartItemRepository.save(newCartItem);

        // product.setQuantity(product.getQuantity() - quantity);  // This will reduce the stock of the product when the item is added to the cart
        // Or we can reduce the stock when the order is placed

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);

        // Return updated cart
        return settingCartDTO(cart);
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if(carts.size()==0) {
            throw new APIException("No cart exist");
        }

        List<CartDTO> cartDTOList = carts.stream().map(cart -> {
            // So the important thing here is, we have product inside CartItem and List of cartItem inside Cart, now in CartDTO we are sending the list of ProductDTO's, so its important to map each product into productDTO and then set it into cartDTO, as product to productDTO wont happen automatically

            // We are getting the list of all the cart items and extracting product from it and converting each one of them into productDTO's

//            List<ProductDTO> productDTOList = cart.getCartItems().stream().map(cartItem ->
//            {
//                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
//                return productDTO;
//            }).toList();
//
//            cartDTO.setProducts(productDTOList);

            return settingCartDTO(cart);
        }).toList();


        return cartDTOList;
    }

    @Override
    public CartDTO getUserCartById(Long userID) {
        Cart cart = cartRepository.findCartByUser_UserId(userID);
//        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
//        List<ProductDTO> productDTOList = cart.getCartItems().stream().map(item -> {
//            return modelMapper.map(item.getProduct(), ProductDTO.class);
//        }).toList();
//        cartDTO.setProducts(productDTOList);

//        return cartDTO;
        return settingCartDTO(cart);
    }

    private CartDTO settingCartDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cart.getCartId());
        cartDTO.setTotalPrice(cart.getTotalPrice());

        List<CartItemDTO> items = cart.getCartItems().stream().map(item -> {

            Product product_temp = item.getProduct();

            ProductDTO productDTO = new ProductDTO(
                    product_temp.getProductId(),
                    product_temp.getProductName(),
                    product_temp.getImage(),
                    product_temp.getDescription(),
                    item.getQuantity(),   // important: cart quantity
                    product_temp.getPrice(),
                    product_temp.getDiscount(),
                    product_temp.getSpecialPrice()
            );

            return new CartItemDTO(
                    item.getCartItemId(),
                    productDTO,
                    item.getQuantity(),
                    item.getDiscount(),
                    item.getProductPrice()
            );

        }).toList();

        cartDTO.setItems(items);
        return cartDTO;
    }

    @Override
    public CartDTO getCart(String emailId) {
        // Cart cart = cartRepository.findCartByUser_EmailAndCartId(emailId, cartId); //Both of these commands works the exact same way, in below one we are explicitly writing the SQL query ourselves

        Cart cart = cartRepository.findCartByUser_Email(emailId);
        if(cart==null) throw new ResourceNotFoundException("Cart", "emailId", emailId);

        // here we are setting the quantity as the actual quantity input by the user, without this, the total available quantity in the stock was getting printed

        return settingCartDTO(cart);
    }

    @Override
    @Transactional
    // IMP : This is important here, because we will have multiple operations within this method. If lets say operation1 and operation2 passed and operation3 gave some exception then operation1 and operation2 will be rolled back. So this annotation packs this method as a single unit, so either whole operations in this method will be executed or none of them will be

    // Or if anything wrong happens with the application and it stopped without this unit of code being totally executed then it will roll back

    // This is important for the updating delicate properties like debiting the money from user's account
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        // This method getLoggedInUser() inside authUtils give logged in user, and then we can use the getter on that object to get the userId;
        Cart cart = getOrCreateCart();
        Long cartId = cart.getCartId();

//        // Getting the cart first, if doenst exist then throw an exception
//        Cart cart = cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("Cart", "cartID", cartId));


        // Retrieving the product details from the database from the id
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productID", productId));


        if(product.getQuantity()==0) {
            throw new APIException(product.getProductName() + " is Not available");
        }

        CartItem cartItem = cartItemRepository.findCartItemsByCart_CartIdAndProduct_ProductId(cartId, productId);

        if(cartItem==null) throw new APIException("Product "+ product.getProductName() + " Not exists in the cart");

        if(product.getQuantity()<cartItem.getQuantity()+ quantity) {
            throw new APIException("Please make an order of the " + product.getProductName() + " less than or equal to quantity : " + product.getQuantity());
        }

        // The quantity cant be negative
        int newQty = cartItem.getQuantity() + quantity;
        if(newQty<0){
            throw new APIException("The resulting quantity cant be negative ");
        }

        if (newQty==0 ){
            deleteProductFromCart(cartId, productId);
        }
        else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);   // If its delete quantity is = -1, so it will decrease the quantity

            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (quantity * cartItem.getProductPrice()));

            cartRepository.save(cart);
        }


        CartItem updatedCartItem = cartItemRepository.save(cartItem);

        // So this cartId will come from the user which is logged in and authenticated, as this is a secure api

        /*
        CartDTO cartDTO =  modelMapper.map(cart, CartDTO.class);
//        List<ProductDTO> productDTOS = cart.getCartItems().stream().map(item-> {
//            ProductDTO prod = modelMapper.map(item.getProduct(), ProductDTO.class);
//            prod.setQuantity(item.getQuantity());   // we need to set the quantity of the each item into its respective product.
//            return prod;
//        }).toList();
//
//        cartDTO.setProducts(productDTOS);
        return cartDTO;

 */
        return settingCartDTO(cart);
    }



    private CartDTO buildCartDTO(Cart cart) {
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

//        List<ProductDTO> productDTOS = cart.getCartItems().stream().map(item -> {
//            ProductDTO prod = modelMapper.map(item.getProduct(), ProductDTO.class);
//            prod.setQuantity(item.getQuantity());
//            return prod;
//        }).toList();
//
//        cartDTO.setProducts(productDTOS);
        return cartDTO;
    }


    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("cart", "cartId" , cartId));

        CartItem existingItem = cartItemRepository.findCartItemsByCart_CartIdAndProduct_ProductId(cartId, productId);
        if (existingItem==null) throw new ResourceNotFoundException("Product", "productId", productId);

        // Before deleting we will update the total price
        cart.setTotalPrice(cart.getTotalPrice() - (existingItem.getProductPrice() * existingItem.getQuantity()));

        cart.getCartItems().remove(existingItem);
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId,productId);

        return "Product with id : " + existingItem.getProduct().getProductId() + " is deleted successfully";
    }

    @Override
    public void updateProductInCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("cart", "cartId", cartId));

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productID", productId));

        CartItem cartItem = cartItemRepository.findCartItemsByCart_CartIdAndProduct_ProductId(cartId
        , productId);

        if(cartItem==null) throw new APIException("Product "  + product.getProductName() + " Not available in the cart");

        // old price of the product is getting removed
        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        // setting the new price of the product
        cartItem.setProductPrice(product.getSpecialPrice());

        // Setting the cart total price again with updated product price
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice()* cartItem.getProductPrice()));

        cartItem = cartItemRepository.save(cartItem);
    }


}
