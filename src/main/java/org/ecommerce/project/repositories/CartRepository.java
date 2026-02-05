package org.ecommerce.project.repositories;

import org.ecommerce.project.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findCartByUser_Email(String userEmail);
}
