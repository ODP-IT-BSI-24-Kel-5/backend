package id.co.bankbsi.e_walled.dto.response;

import id.co.bankbsi.e_walled.models.Wallets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class WalletsResponse extends Response {
    private List<Wallets> wallets;

    public WalletsResponse(String status, String message, HttpStatus statusCode, List<Wallets> wallets) {
        super(status, message, statusCode);
        this.wallets = wallets;
    }

    public static WalletsResponse success( List<Wallets> wallets) {
        return new WalletsResponse("success", "Wallet list retrieved successfully!", HttpStatus.OK, wallets);
    }
}
