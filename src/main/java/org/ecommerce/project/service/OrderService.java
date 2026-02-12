package org.ecommerce.project.service;

import jakarta.transaction.Transactional;
import org.ecommerce.project.payload.OrderDTO;
import org.ecommerce.project.payload.OrderRequestDTO;

public interface OrderService {
    @Transactional
    OrderDTO placeOrder(String emailIdOfLoggedInUser, String paymentMethod, OrderRequestDTO orderRequestDTO);
}
