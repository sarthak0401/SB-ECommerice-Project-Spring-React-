package org.ecommerce.project.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecommerce.project.model.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@NoArgsConstructor
@Data
public class UserDetailsImplementation implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImplementation(Long id, String username, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // We are creating this build method to convert this User object into UserDetails type of the object
    public static UserDetailsImplementation build(User user){

        List<GrantedAuthority> authorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRoleName().name())).collect(Collectors.toList());
        // From user we are getting the roles, and then converting it into stream and for each role we are creating the simpleGrantedAuthority object

        return new UserDetailsImplementation(
          user.getUserId(),
          user.getUserName(),
          user.getEmail(),
          user.getPassword(),
                authorities
        );
        // Here we are returning the object of UserDetailsImplementation which is created by calling the constructor of this class and setting up attributes values from the User class's reference into it.
        // Basically we are converting the user object of User entity into the UserDetailsImplementation object, and now this will do the conversion between UserDetailsService interface's custom implementation that we'll write
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o==null || getClass()!=o.getClass()) return false;
        UserDetailsImplementation user = (UserDetailsImplementation) o;
        return Objects.equals(id, user.id);
    }
}
