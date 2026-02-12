package org.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// This class represents the response
// This is representing individual item in order
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long orderItemId;
    private ProductDTO productDTO; // This have the product info
    private Integer quantity;
    private Double discount;
    private Double orderedProductPrice;
}
