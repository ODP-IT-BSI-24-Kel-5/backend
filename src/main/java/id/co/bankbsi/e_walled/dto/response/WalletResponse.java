package id.co.bankbsi.e_walled.dto.response;

import id.co.bankbsi.e_walled.models.Wallets;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class WalletResponse extends Response {
    private Wallets wallets;

    public WalletResponse(String status, String message, HttpStatus statusCode, Wallets wallets) {
        super(status, message, statusCode);
        this.wallets = wallets;
    }

    public static WalletResponse success(Wallets wallets) {
        return new WalletResponse("success", "Wallet retrieved successfully!", HttpStatus.OK, wallets);
    }

    public static WalletResponse successCreate(Wallets wallets) {
        return new WalletResponse("success", "Wallet created successfully!", HttpStatus.CREATED, wallets);
    }
}