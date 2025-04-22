package id.co.bankbsi.e_walled.annotations;

import id.co.bankbsi.e_walled.validations.CreateTransactionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CreateTransactionValidator.class)
public @interface ValidTransactions {
    String message() default "Invalid transaction request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
