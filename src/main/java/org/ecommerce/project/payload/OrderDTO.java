package org.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
// Represents the response
// Class to represent the order details for the frontend
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private String email;
    private List<OrderItemDTO> orderItems;
    private LocalDate orderDate;
    private PaymentDTO payment;
    private Double totalAmount;
    private String orderStatus;
    private Long addressId;
    // So rather than passing the whole address, we will just send the addressId, so to which address this order was related to.

    // With DTO we can customize what are the things we want to send/receive
}
