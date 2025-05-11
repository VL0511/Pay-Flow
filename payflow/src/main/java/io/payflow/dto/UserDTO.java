package io.payflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "E-mail is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
