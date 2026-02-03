package org.ecommerce.project.security.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    public String username;
    public String jwtToken;
    public List<String> roles;
}
