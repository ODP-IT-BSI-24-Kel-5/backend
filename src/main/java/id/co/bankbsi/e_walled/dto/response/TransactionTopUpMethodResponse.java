package id.co.bankbsi.e_walled.dto.response;

import id.co.bankbsi.e_walled.models.TransactionCategories;
import id.co.bankbsi.e_walled.models.TransactionTopUpMethods;
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
public class TransactionTopUpMethodResponse extends Response {
    private List<TransactionTopUpMethods> transactionTopUpMethods;

    public TransactionTopUpMethodResponse(String status, String message, HttpStatus statusCode, List<TransactionTopUpMethods> transactionTopUpMethods) {
        super(status, message, statusCode);
        this.transactionTopUpMethods = transactionTopUpMethods;
    }

    public static TransactionTopUpMethodResponse success(List<TransactionTopUpMethods> transactionTopUpMethods) {
        return new TransactionTopUpMethodResponse("success", "User retrieved successfully!", HttpStatus.OK, transactionTopUpMethods);
    }
}