package id.co.bankbsi.e_walled.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.util.List;

@Setter
@Getter
@Accessors(chain = true)
public class WalletResponseGeneral extends Response {
    private WalletsGeneral wallets;

    public WalletResponseGeneral(String status, String message, HttpStatus statusCode, WalletsGeneral wallets) {
        super(status, message, statusCode);
        this.wallets = wallets;
    }

    public static WalletResponseGeneral success(WalletsGeneral wallets) {
        return new WalletResponseGeneral("success", "Wallet retrieved successfully!", HttpStatus.OK, wallets);
    }


    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class WalletsGeneral {
        private String name;
        private String userName;
        private String number;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class WalletsResponseGeneral extends Response {
        private List<WalletsGeneral> wallets;

        public WalletsResponseGeneral(String status, String message, HttpStatus statusCode, List<WalletsGeneral> wallets) {
            super(status, message, statusCode);
            this.wallets = wallets;
        }

        public static WalletsResponseGeneral success(List<WalletsGeneral> wallets) {
            return new WalletsResponseGeneral("success", "Wallet retrieved successfully!", HttpStatus.OK, wallets);
        }
    }
}
