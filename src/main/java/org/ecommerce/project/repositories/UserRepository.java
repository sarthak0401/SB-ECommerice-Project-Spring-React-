package org.ecommerce.project.repositories;

import org.ecommerce.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUserName(String userName);
    // With Optional wrapper, we are saying that the method may contain User or it may be Empty, its uncertain. And we can handle the Empty logic in the service layer.
    //  User findUserByUserName(String userName); -> If we write something like this, then its sure that the User will exist, if still user is not found, it will return NULL and set it to the User type so defined, and if we try to access user.password or something, it will give NullPointerException

    boolean existsUserByUserName(String userName);

    boolean existsUserByEmail(String email);
}
