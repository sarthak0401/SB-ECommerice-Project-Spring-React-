package org.ecommerce.project.repositories;

import org.ecommerce.project.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("""
        SELECT oi FROM OrderItem oi JOIN FETCH oi.product
        WHERE oi.order.orderId = :orderId
        """)
    List<OrderItem> findByOrderIdWithProduct(Long orderId);

}
