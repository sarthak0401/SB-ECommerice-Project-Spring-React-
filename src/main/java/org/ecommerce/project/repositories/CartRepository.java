package org.ecommerce.project.repositories;

import org.ecommerce.project.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findCartByUser_Email(String userEmail);

    Cart findCartByUser_UserId(Long userUserId);

//    Cart findCartByUser_EmailAndCartId(String userEmail, Long cartId);

    @Query("SELECT c FROM Cart c WHERE c.user.email=?1 AND c.cartId=?2")
    Cart findCartByEmailAndCartId(String userEmail, Long cartId);

//    Long user(User user);
}
