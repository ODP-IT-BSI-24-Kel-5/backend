package id.co.bankbsi.e_walled.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Email required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
