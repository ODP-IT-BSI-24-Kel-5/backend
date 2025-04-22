package id.co.bankbsi.e_walled.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePinRequest {
    @NotBlank(message = "Pin required")
    private String pin;

    @NotBlank(message = "Confirmation pin is required")
    private String confirmationPin;

    @NotBlank(message = "Confirmation pin is required")
    private String oldPin;
}


