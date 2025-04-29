package id.co.bankbsi.e_walled.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Full Name required")
    private String fullName;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email required")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long, include uppercase letter, number, and special character"
    )
    private String password;

    @NotBlank(message = "Confirmation password is required")

    private String confirmationPassword;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^(\\+62|62|0)8[1-9][0-9]{7,12}$",
            message = "Phone number must be a valid number"
    )
    private String mobilePhone;


    @Pattern(
            regexp = "^(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|jpeg|png|gif|bmp|webp)$",
            message = "Image URL must end with a valid image extension (.jpg, .png, etc.)"
    )
    private String imageUrl;
}
