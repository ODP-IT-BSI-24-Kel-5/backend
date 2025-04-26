package id.co.bankbsi.e_walled.services;

import id.co.bankbsi.e_walled.dto.response.*;
import id.co.bankbsi.e_walled.exceptions.BadRequestException;
import id.co.bankbsi.e_walled.exceptions.InternalServerException;
import id.co.bankbsi.e_walled.exceptions.NotFoundException;
import id.co.bankbsi.e_walled.models.*;
import id.co.bankbsi.e_walled.repositories.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionTopUpMethodService {
    private final ModelMapper modelMapper;

    @Autowired
    private final TransactionCategoryRepository transactionCategoryRepository;
    @Autowired
    private TransactionTopUpMethodsRepository transactionTopUpMethodsRepository;


    public Response getTransactionTopUpMethod(){
        try {
            List<TransactionTopUpMethods> transactionTopUpMethods = transactionTopUpMethodsRepository.findAll();
            return TransactionTopUpMethodResponse.success(transactionTopUpMethods);
        } catch (NotFoundException | BadRequestException e) {
            throw e; // Let handled exceptions bubble up
        } catch (Exception e) {
            throw new InternalServerException("Failed to fetch user, internal server error!");
        }
    }
}
