package org.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long OrderItemId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    // Many order item can have the same product, many order items think belonging to different customers

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Integer quantity;

    private Double discount;

    private Double orderedProductPrice;
}
