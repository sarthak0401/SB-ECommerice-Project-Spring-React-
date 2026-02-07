package org.ecommerce.project.util;

import org.ecommerce.project.model.User;
import org.ecommerce.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils{
    @Autowired
    UserRepository userRepository;

    public String loggedInUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByUserName(authentication.getName()).orElseThrow(()-> new UsernameNotFoundException("User not found"));

        return user.getEmail();
    }

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUserByUserName(authentication.getName()).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return user;
    }
}
