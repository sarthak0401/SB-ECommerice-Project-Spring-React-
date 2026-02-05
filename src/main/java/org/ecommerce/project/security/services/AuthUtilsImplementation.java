package org.ecommerce.project.security.services;

import org.ecommerce.project.model.User;
import org.springframework.stereotype.Service;

@Service
public class AuthUtilsImplementation implements AuthUtils{
    @Override
    public String loggedInUserEmail() {
        return "";
    }

    @Override
    public User getLoggedInUser() {
        return null;
    }
}
