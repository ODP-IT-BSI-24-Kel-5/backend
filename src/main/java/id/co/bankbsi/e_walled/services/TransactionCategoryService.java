package id.co.bankbsi.e_walled.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.bankbsi.e_walled.dto.request.CreateTransactionRequest;
import id.co.bankbsi.e_walled.dto.request.SortRequest;
import id.co.bankbsi.e_walled.dto.request.TransactionFilterRequest;
import id.co.bankbsi.e_walled.dto.request.TransactionsRequest;
import id.co.bankbsi.e_walled.dto.response.*;
import id.co.bankbsi.e_walled.exceptions.BadRequestException;
import id.co.bankbsi.e_walled.exceptions.InternalServerException;
import id.co.bankbsi.e_walled.exceptions.NotFoundException;
import id.co.bankbsi.e_walled.models.*;
import id.co.bankbsi.e_walled.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionCategoryService {
    private final ModelMapper modelMapper;

    @Autowired
    private final TransactionCategoryRepository transactionCategoryRepository;


    public Response getTransactionCategory(){
        try {
            List<TransactionCategories> transactionCategory = transactionCategoryRepository.findAll();
            return TransactionCategoriesResponse.success(transactionCategory);
        } catch (NotFoundException | BadRequestException e) {
            throw e; // Let handled exceptions bubble up
        } catch (Exception e) {
            throw new InternalServerException("Failed to fetch user, internal server error!");
        }
    }
}
