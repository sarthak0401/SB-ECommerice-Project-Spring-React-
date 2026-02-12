package org.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// Represents the response

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long paymentId;
    // It represents the single payment
    private String paymentMethod;
    private String pgPaymentId;
    private String pgResponseMessage;
    private String pgName;
}
