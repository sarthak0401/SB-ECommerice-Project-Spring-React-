package org.ecommerce.project.controller;

import jakarta.validation.Valid;
import org.ecommerce.project.model.AppRoles;
import org.ecommerce.project.model.Role;
import org.ecommerce.project.model.User;
import org.ecommerce.project.repositories.RoleRepository;
import org.ecommerce.project.repositories.UserRepository;
import org.ecommerce.project.security.jwt.JwtUtils;
import org.ecommerce.project.security.request.LoginRequest;
import org.ecommerce.project.security.request.SignupRequest;
import org.ecommerce.project.security.response.MessageResponse;
import org.ecommerce.project.security.response.UserInfoResponse;
import org.ecommerce.project.security.services.UserDetailsImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class AuthenticationController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authentication(@RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

        }catch (AuthenticationException e){
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad Credentials");
            map.put("Status" , false);

            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImplementation userDetails = (UserDetailsImplementation) authentication.getPrincipal();
        //this is the custom UserDetails implementation

        // We need to get jwt token, username and roles from the userDetails and then send them as the response
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).toList();
        // This is getting the role from every item inside the List

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), jwtToken, roles); // This is the all args constructor of lombok from UserDetailsImplementation class that we have defined

        return ResponseEntity.ok(response);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest){
        if(userRepository.existsUserByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error : Username already Taken"));
        }

        if(userRepository.existsUserByEmail(signUpRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error : Email is already taken"));
        }

        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword())
        );


        // This roleStr can be empty as well, as its NOT mandatory for the user to pass in the roles, Its not defined in the model

        Set<String> roleStr = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        if(roleStr==null){ // if role is not passed by user, then we set the default role here
            Role userRole = roleRepository.findRolesByRoleName(AppRoles.ROLE_USER).orElseThrow(()-> new RuntimeException("Error : Role is not found"));
                roles.add(userRole);
        }
        else{ // user passed in some roles through the SignupRequest DTO
                roleStr.forEach(role -> {
                    if(role.toLowerCase().contains("admin")){
                        Role adminRole = roleRepository.findRolesByRoleName(AppRoles.ROLE_ADMIN).orElseThrow(()-> new RuntimeException("Error : Role is not found"));
                        roles.add(adminRole);
                    }
                    else if(role.toLowerCase().contains("seller")){
                        Role sellerRole = roleRepository.findRolesByRoleName(AppRoles.ROLE_SELLER).orElseThrow(()-> new RuntimeException("Error : Role is not found"));
                        roles.add(sellerRole);
                    }

                    // If user sends role as user, or any other role which is not valid, then by default it will be set to the user role, therefore this else block

                    else  {
                        Role userRole = roleRepository.findRolesByRoleName(AppRoles.ROLE_USER).orElseThrow(()-> new RuntimeException("Error : Role is not found"));
                        roles.add(userRole);
                    }
                });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User Registered Successfully"));
    }
}




