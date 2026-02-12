package org.ecommerce.project.controller;

import org.ecommerce.project.payload.OrderDTO;
import org.ecommerce.project.payload.OrderRequestDTO;
import org.ecommerce.project.service.OrderService;
import org.ecommerce.project.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtils authUtils;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProduct(
            @PathVariable String paymentMethod,
            @RequestBody OrderRequestDTO orderRequestDTO
            ){
        String emailId_Of_loggedInUser = authUtils.loggedInUserEmail();
        OrderDTO orderInfo =  orderService.placeOrder(emailId_Of_loggedInUser,paymentMethod, orderRequestDTO);

        return new ResponseEntity<>(orderInfo, HttpStatus.OK);
    }
}
