package org.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Represent the request, we will be needing these things from the frontend

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private Long addressId;
    private String paymentMethod;
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;
    private String pgPaymentId;
}
