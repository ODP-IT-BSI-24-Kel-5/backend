package id.co.bankbsi.e_walled.dto.request;

import id.co.bankbsi.e_walled.models.TransactionTypes;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionsRequest {
    private String id;
    private String transactionNumber;
    private String associateWallet;
    private String wallet;
    private String walletNumber;
    private TransactionTypes type;
    private String categoryName;
    private String startDate;
    private String endDate;
    private Integer page;
    private Integer size;
    private String sort;
    private String search;
}


