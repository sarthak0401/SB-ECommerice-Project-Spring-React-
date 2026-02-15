package org.ecommerce.project.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ecommerce.project.security.services.UserDetailsServiceImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // if we write component, then it is spring managed class, so we can use Autowired annotation to pass the bean, without it, we cant use Autowired

@Order(2)
// This OncePerRequestFilter runs once only with every request
public class AuthTokenFilter extends OncePerRequestFilter {
// We created our own custom filter

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    @Lazy
    private UserDetailsServiceImplementation userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.debug("AuthTokenFiler is called for URI {}", request.getRequestURI());

        try {
            String jwt = parseJwt(request);
            if(jwt!=null && jwtUtils.validateJWTToken(jwt)){
                String username = jwtUtils.getUsernameFromJWTToken(jwt);  // Getting the username from token
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);  // Getting the userdetails from the database from the username, since jwt only stores username

                // Then we will create an authentication object, which needs to be set in security context. To mark the request as authenticated

                // This is an authentication object
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,null,userDetails.getAuthorities()
                );
                //NOTE : with JWT(stateless), Authentication object is set in SecurityContext on EVERY request, here the request is already valid, since token is valid, but still we created the authentication object and stored it in SecurityContext EVERY time the request is made. Its because the SecurityContext is cleared after each request, it lives only for each request lifecycle, after the response the SecurityContext cleared automatically (Its because the server is stateless and dont store any user info)

                // SecurityContextHolder.getContext().setAuthentication(authenticationToken); This DOESN'T store the user forever. It simply says that for THIS PARTICULAR request Thread, user is authenticated.

                // So with Jwt Every request -> Fresh identity verification
                // Authentication object does NOT stay in SecurityContext until JWT expires, it ONLY lives for the duration of ONE request

                // we are attaching the request details to the authentication object, so like whatever the request have it is passed on to the authentication object
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // We need to set this object to the Security Content of spring security
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                // So like once the authentication object is set into the securityContext then that particular request is accepted / that request is authenticated. Then immediately the authentication object is deleted from the securityContext once the request is processed and response is send back to client



                logger.debug("Roles from JWT : {}", userDetails.getAuthorities());

            }
        }
        catch (Exception e){
            logger.error("Cannot set user authentication :{}",e);
            // So when the validateJWT token method fails, then no authentication object is set into securityContext, spring treats request as anonymous and AuthenticationEntryPoint is trigerred and 401 returned
        }

        // this specifies spring security to continue filter chain and execute any filters that are pending
        filterChain.doFilter(request, response); // with this all the rest of the filters other than this custom authentication one will continue executing normally
    }


    private String parseJwt(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromCookie(request);
        logger.debug("AuthTokenFiler.java : {}", jwt);
        return jwt;
    }
}
