package id.co.bankbsi.e_walled.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreatePinRequest {
    @NotBlank(message = "Pin required")
    @Pattern(
            regexp = "[0-9]{6,6}$",
            message = "Phone need to be a 6 digits number!"
    )
    private String pin;

    @NotBlank(message = "Confirmation pin is required")
    @Pattern(
            regexp = "[0-9]{6,6}$",
            message = "Phone need to be a 6 digits number!"
    )
    private String confirmationPin;

    @Pattern(
            regexp = "[0-9]{6,6}$",
            message = "Phone need to be a 6 digits number!"
    )
    private String oldPin;
}


