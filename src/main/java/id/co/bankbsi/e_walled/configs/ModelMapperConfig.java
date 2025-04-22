package id.co.bankbsi.e_walled.configs;

import id.co.bankbsi.e_walled.dto.response.TransactionResponse;
import id.co.bankbsi.e_walled.models.Transactions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper =  new ModelMapper();

        modelMapper.typeMap(Transactions.class, TransactionResponse.class).addMappings(mapper -> {
            mapper.map(src -> src.getWallet().getNumber(), TransactionResponse::setWallet);
            mapper.map(src -> src.getWallet().getName(), TransactionResponse::setWalletName);
            mapper.map(src -> src.getAssociateWallet().getNumber(), TransactionResponse::setAssociateWallet);
            mapper.map(src -> src.getAssociateWallet().getName(), TransactionResponse::setAssociateName);
            mapper.map(src -> src.getCategory().getName(), TransactionResponse::setCategory);
        });


        return modelMapper;
    }
}