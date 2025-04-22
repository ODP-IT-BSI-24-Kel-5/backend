package id.co.bankbsi.e_walled.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import id.co.bankbsi.e_walled.annotations.ValidTransactions;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {
    @Positive(message = "Amount must be greater than 0")
    @JsonProperty(required = true)
    private Long amount;

    private String notes;
    private Long category;

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @ValidTransactions
    public static class CreateTransactionTransferRequest extends CreateTransactionRequest {
        @JsonProperty(required = true)
        private String pin;
        @JsonProperty(required = true)
        private String senderAccount;
        @JsonProperty(required = true)
        private String acquirerAccount;
    }


    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    public static class CreateTransactionTopUpRequest extends CreateTransactionRequest {
        @JsonProperty(required = true)
        private String pin;
        @JsonProperty(required = true)
        private String acquirerAccount;

        @JsonProperty(required = true)
        private String via;
    }
}


