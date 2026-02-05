package org.ecommerce.project.security.services;

import org.ecommerce.project.model.User;

public interface AuthUtils {
    String loggedInUserEmail();

    User getLoggedInUser();
}
