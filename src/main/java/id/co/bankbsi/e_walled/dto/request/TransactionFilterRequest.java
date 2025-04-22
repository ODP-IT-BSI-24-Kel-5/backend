package id.co.bankbsi.e_walled.dto.request;

import id.co.bankbsi.e_walled.models.TransactionTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFilterRequest {
    private String startDate;
    private String transactionNumber;
    private String associateWallet;
    private String wallet;
    private String walletNumber;
    private String endDate;
    private TransactionTypes type;
    private String categoryName;
    private String search;
}