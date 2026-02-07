package org.ecommerce.project.security;

import org.ecommerce.project.model.AppRoles;
import org.ecommerce.project.model.Role;
import org.ecommerce.project.model.User;
import org.ecommerce.project.repositories.RoleRepository;
import org.ecommerce.project.repositories.UserRepository;
import org.ecommerce.project.security.jwt.AuthEntryPoint;
import org.ecommerce.project.security.jwt.AuthTokenFilter;
import org.ecommerce.project.security.services.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {
    @Autowired
    UserDetailsServiceImplementation userDetailsServiceImplementation;

    @Autowired
    private AuthEntryPoint unauthorizedhandler;

    @Autowired
    AuthTokenFilter authTokenFilter;


    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsServiceImplementation); // setting our custom userDetailsServiceImplementation into the authentication provider so that it can look into the database using the methods that we defined
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/h2-console", "/h2-console/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
//                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").permitAll()   // Not good practice for production, we are doing it for development ease
                .requestMatchers("/api/test/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .anyRequest().authenticated());


        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedhandler)); // We have defined an unauthorized handler, so if any unauthorized thing happens using this exception handler

        http.csrf(csrf -> csrf.disable());

        http.authenticationProvider(authenticationProvider()); // This argument is coming from the authentication bean we created above

        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // For h2-console rendering
        http.headers(headers -> headers.frameOptions(
                frameOptionsConfig -> frameOptionsConfig.sameOrigin()
        ));

        return http.build();
    }

    // Anything mentioned inside WebSecurityCustomizer ignore will be completely bypassed by the Spring security, spring security wont even check them. But whatever we write to permitAll() in requestMatchers in SecurityFilterChain, will first be checked by spring security, and then it will permits it
    // So basically spring security comes into picture, But when things are written under WebSecurityCustomizer, spring wont even check it, its directly bypassed without spring security intervention
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web -> web.ignoring().requestMatchers(
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "/h2-console/**"
        ));
    }


    // Initializing few users
    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Retrieve or create roles
            Role userRole = roleRepository.findRolesByRoleName(AppRoles.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role(AppRoles.ROLE_USER);
                        return roleRepository.save(newUserRole);
                    });

            Role sellerRole = roleRepository.findRolesByRoleName(AppRoles.ROLE_SELLER)
                    .orElseGet(() -> {
                        Role newSellerRole = new Role(AppRoles.ROLE_SELLER);
                        return roleRepository.save(newSellerRole);
                    });

            Role adminRole = roleRepository.findRolesByRoleName(AppRoles.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role(AppRoles.ROLE_ADMIN);
                        return roleRepository.save(newAdminRole);
                    });

            Set<Role> userRoles = Set.of(userRole);
            Set<Role> sellerRoles = Set.of(sellerRole);
            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);


            // Create users if not already present
            if (!userRepository.existsUserByUserName("user1")) {
                User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"));
                userRepository.save(user1);
            }

            if (!userRepository.existsUserByUserName("seller1")) {
                User seller1 = new User("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
                userRepository.save(seller1);
            }

            if (!userRepository.existsUserByUserName("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
                userRepository.save(admin);
            }

            // Update roles for existing users after saving them
            userRepository.findUserByUserName("user1").ifPresent(user -> {
                user.setRoles(userRoles);
                userRepository.save(user);
            });

            userRepository.findUserByUserName("seller1").ifPresent(seller -> {
                seller.setRoles(sellerRoles);
                userRepository.save(seller);
            });

            userRepository.findUserByUserName("admin").ifPresent(admin -> {
                admin.setRoles(adminRoles);
                userRepository.save(admin);
            });
        };
    }
}
