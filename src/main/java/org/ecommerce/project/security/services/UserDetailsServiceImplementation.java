package org.ecommerce.project.security.services;

import jakarta.transaction.Transactional;
import org.ecommerce.project.model.User;
import org.ecommerce.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// UserDetailsService this class fetches the user from the database, part of the authentication.
// So when any user attempts to log in spring security calls this class to lookup the user in the database, and get their roles and authorities to perform the authentication and authorization
@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional // this ensures the db operation is handled in a transaction way : the entire operation must be carried out fully, and if there is any error, then entire operation is rolled back
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUserName(username).orElseThrow(()-> new UsernameNotFoundException("User Not found with this username : " + username));
        // We handled this user Not found thing by doing something like .orElseThrow(), as it is defined as Optional in userRepository, so we need to explicitly handle the case for user not found, it wont set the user to null

        return UserDetailsImplementation.build(user);
        // We are creating the object of UserDetailsImplementation which is the implementation of UserDetails interface with the use of user object.
        // So the userRepository will find the user based on the username and then that object of User class will be converted into UserDetailsImplementation type (which is the same type as UserDetails interface, parent-child relation) and retuned.
    }
}
