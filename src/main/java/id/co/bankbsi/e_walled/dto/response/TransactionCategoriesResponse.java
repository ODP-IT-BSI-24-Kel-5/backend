package id.co.bankbsi.e_walled.dto.response;

import id.co.bankbsi.e_walled.models.TransactionCategories;
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
public class TransactionCategoriesResponse extends Response {
    private List<TransactionCategories> transactionCategories;

    public TransactionCategoriesResponse(String status, String message, HttpStatus statusCode, List<TransactionCategories> transactionCategories) {
        super(status, message, statusCode);
        this.transactionCategories = transactionCategories;
    }

    public static TransactionCategoriesResponse success(List<TransactionCategories> transactionCategories) {
        return new TransactionCategoriesResponse("success", "User retrieved successfully!", HttpStatus.OK, transactionCategories);
    }
}