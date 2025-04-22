package id.co.bankbsi.e_walled.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CreateWalletRequest {
    @JsonProperty(required = false)
    private String name;
    @JsonProperty(required = false)
    private Boolean main;
}