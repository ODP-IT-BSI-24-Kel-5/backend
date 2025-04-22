package id.co.bankbsi.e_walled.validations;


import id.co.bankbsi.e_walled.annotations.ValidEnum;
import id.co.bankbsi.e_walled.models.TransactionTypes;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum<?>> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }


    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return enumClass.isAssignableFrom(value.getClass());
    }
}
