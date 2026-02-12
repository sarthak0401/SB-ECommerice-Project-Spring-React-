package org.ecommerce.project.service;

import jakarta.transaction.Transactional;
import org.ecommerce.project.exceptions.APIException;
import org.ecommerce.project.exceptions.ResourceNotFoundException;
import org.ecommerce.project.model.*;
import org.ecommerce.project.payload.OrderDTO;
import org.ecommerce.project.payload.OrderItemDTO;
import org.ecommerce.project.payload.OrderRequestDTO;
import org.ecommerce.project.payload.ProductDTO;
import org.ecommerce.project.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImplementation implements OrderService{
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailIdOfLoggedInUser, String paymentMethod, OrderRequestDTO orderRequestDTO) {
        // Getting the cart of the user
        Cart cart = cartRepository.findCartByUser_Email(emailIdOfLoggedInUser);
        if(cart==null) throw new ResourceNotFoundException("Cart", "email", emailIdOfLoggedInUser);
        // If the exception happens, we stop right here, rest of the things in the method will not execute


        // Getting the address from the Address entity using addressId passed from orderRequestDTO
        Address address = addressRepository.findById(orderRequestDTO.getAddressId()).orElseThrow(()-> new ResourceNotFoundException("Address", "addressId", orderRequestDTO.getAddressId()));

        // Creating order object and creating payment info object and mapping them both
        Order order = new Order();
        order.setEmail(emailIdOfLoggedInUser);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted");
        order.setAddress(address);


        Payment payment = new Payment(
                paymentMethod,
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage(),
                orderRequestDTO.getPgName()
        );

        payment.setOrder(order);
        paymentRepository.save(payment);

        // Before setting the payment to the order, we need to save it to the database first
        order.setPayment(payment);

        // Saving the order
        Order savedOrder = orderRepository.save(order);


        // We need to get the items from the cart (its present in the list of CartItems) and add them into the orderItems
        List<CartItem> cartItemList = cart.getCartItems();
        if(cartItemList.isEmpty()) throw new APIException("Cart is empty!");

        // Creating the OrderItem list
        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItem cartItem : cartItemList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);


            orderItems.add(orderItem);
        };

        orderItemRepository.saveAll(orderItems);
        orderItems = orderItemRepository.findByOrderIdWithProduct(savedOrder.getOrderId());


        // Now payment, order and orderItem are saved in the database


        // Creating the order summary now
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);

        List<OrderItemDTO> itemDTOs = orderItems.stream()
                .map(item -> {
                    ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                    // we need to convert the product into productDTO explicitly as in OrderItemDTO we need ProductDTO not Product, So modelMapper wont map them directly, modelmapper wont convert into nested DTO's directly
                    OrderItemDTO orderItemDTO = modelMapper.map(item, OrderItemDTO.class);
                    orderItemDTO.setProductDTO(productDTO);
                    return orderItemDTO;
                })
                .toList();

        orderDTO.setOrderItems(itemDTOs);
        orderDTO.setAddressId(address.getAddressId());


        // Now we need to update the product stock
        List<CartItem> items = new ArrayList<>(cart.getCartItems());

        for (CartItem item : items) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);
        }



        // removing the mapping of cart with item, so we can delete the items from cart, CartItem is the owning side see in Cart model

        for (CartItem item : items) {
            item.setCart(null);
        }

        // Now we will remove the items from the cart (clearing the cart)
         cart.getCartItems().clear();


        cart.setTotalPrice(0.0); // setting the total price of cart back to 0, after order is placed
        cartRepository.save(cart);

        return orderDTO;
    }
}
// we needed transactional annotation here because of the series of steps here, we can let half of the steps happen and maybe due to some error half of the steps didnt get executed, we want complete execution, therefore we have transactional annotation above this method