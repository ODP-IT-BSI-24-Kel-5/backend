package id.co.bankbsi.e_walled.converters;
import id.co.bankbsi.e_walled.models.TransactionTypes;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToTransactionTypeConverter implements Converter<String, TransactionTypes> {
    @Override
    public TransactionTypes convert(String source) {
        return TransactionTypes.fromValue(source);
    }
}