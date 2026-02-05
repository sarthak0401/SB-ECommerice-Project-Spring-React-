package org.ecommerce.project.repositories;

import org.ecommerce.project.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findCartItemsByCart_CartIdAndProduct_ProductId(Long cartCartId, Long productProductId);
}
