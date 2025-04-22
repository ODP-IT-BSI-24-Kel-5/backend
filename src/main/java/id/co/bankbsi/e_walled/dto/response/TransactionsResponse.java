package id.co.bankbsi.e_walled.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class TransactionsResponse extends Response {
    private TransactionResponse transactions;

    public TransactionsResponse(String status, String message, HttpStatus statusCode, TransactionResponse transactions) {
        super(status, message, statusCode);
        this.transactions = transactions;
    }

    public static TransactionsResponse success(TransactionResponse transactions) {
        return new TransactionsResponse("success", "Transaction retrieved successfully!", HttpStatus.OK, transactions);
    }

    public static TransactionsResponse successCreate(TransactionResponse transactions) {
        return new TransactionsResponse("success", "Transaction created successfully!", HttpStatus.CREATED, transactions);
    }
}