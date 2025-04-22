package id.co.bankbsi.e_walled.validations;

import id.co.bankbsi.e_walled.annotations.ValidTransactions;
import id.co.bankbsi.e_walled.dto.request.CreateTransactionRequest;
import id.co.bankbsi.e_walled.dto.response.TransactionCategoriesResponse;
import id.co.bankbsi.e_walled.models.TransactionCategories;
import id.co.bankbsi.e_walled.models.Wallets;
import id.co.bankbsi.e_walled.repositories.TransactionCategoryRepository;
import id.co.bankbsi.e_walled.repositories.WalletRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CreateTransactionValidator implements ConstraintValidator<ValidTransactions, CreateTransactionRequest.CreateTransactionTransferRequest> {

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionCategoryRepository transactionCategoryRepository;

    @Override
    public boolean isValid(CreateTransactionRequest.CreateTransactionTransferRequest req, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        boolean validFields = checkRequiredFields(req, context);
        if (!validFields) return false;

        Wallets sender = walletRepository.findByNumber(req.getSenderAccount());
        Wallets acquirer = walletRepository.findByNumber(req.getAcquirerAccount());

        if (!validateWalletsExist(sender, acquirer, context)) return false;

        if (req.getCategory() != null) {
            if (req.getCategory() == 0) {
                addViolation(context, "Category", "Not a valid category");
                return false; // Invalid category, return false
            }

            Optional<TransactionCategories> category = transactionCategoryRepository.findById(req.getCategory());
            if (!category.isPresent()) {
                addViolation(context, "Category", "Not a valid category");
                return false; // Category not found, return false
            }
        }

        return true;
    }

    private boolean checkRequiredFields(CreateTransactionRequest.CreateTransactionTransferRequest req, ConstraintValidatorContext context) {
        boolean isValid = true;


        if (req.getSenderAccount() == null || req.getSenderAccount().isBlank()) {
            addViolation(context, "senderAccount", "Sender account is required");
            isValid = false;
        }

        if (req.getAcquirerAccount() == null || req.getAcquirerAccount().isBlank()) {
            addViolation(context, "acquirerAccount", "Acquirer account is required");
            isValid = false;
        }

        if (req.getAmount() == null) {
            addViolation(context, "amount", "Amount is required");
            isValid = false;
        }

        if (req.getSenderAccount() != null && req.getSenderAccount().isEmpty()) {
            addViolation(context, "senderAccount", "Sender account must be a non-empty string");
        }


        if (req.getAcquirerAccount() != null && req.getAcquirerAccount().isEmpty()) {
            addViolation(context, "acquirerAccount", "Acquirer account must be a non-empty string");
        }

        // Validate type for notes (String) - nullable
        if (req.getNotes() != null && req.getNotes().isEmpty()) {
            addViolation(context, "notes", "Notes must be a non-empty string if provided");
        }


        return isValid;
    }

    private boolean validateWalletsExist(Wallets sender, Wallets acquirer, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (sender == null) {
            addViolation(context, "senderAccount", "Sender account not found");
            isValid = false;
        }

        if (acquirer == null) {
            addViolation(context, "acquirerAccount", "Acquirer account not found");
            isValid = false;
        }

        return isValid;
    }

    private void addViolation(ConstraintValidatorContext context, String field, String message) {
        if (field != null) {
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(field)
                    .addConstraintViolation();
        } else {
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
        }
    }
}
