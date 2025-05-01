package id.co.bankbsi.e_walled.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.co.bankbsi.e_walled.models.TransactionCategories;
import id.co.bankbsi.e_walled.models.TransactionTypes;
import id.co.bankbsi.e_walled.models.Transactions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private UUID id;
    private String transactionNumber;
    private Long amount;
    private LocalDateTime createdAt;
    private String wallet;
    private String walletName;      // ← Add this
    private String associateWallet;
    private String associateName;
    private TransactionTypes type;
    private String notes;
    private String description;
    private String category;
    private String categoryIcon;
    private String method;
    private String imageReceipt;
}