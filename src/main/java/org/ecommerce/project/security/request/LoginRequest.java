package org.ecommerce.project.security.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

// Generated getter and setters for each of the attribute using lombok -> @Data annotation
@Data
@AllArgsConstructor
public class LoginRequest {
    @NotBlank
    public String username;

    @NotBlank
    public String password;
}
