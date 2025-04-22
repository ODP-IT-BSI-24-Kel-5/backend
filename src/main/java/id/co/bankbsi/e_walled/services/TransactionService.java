package id.co.bankbsi.e_walled.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.bankbsi.e_walled.dto.TransactionDTO;
import id.co.bankbsi.e_walled.dto.request.CreateTransactionRequest;
import id.co.bankbsi.e_walled.dto.request.SortRequest;
import id.co.bankbsi.e_walled.dto.request.TransactionFilterRequest;
import id.co.bankbsi.e_walled.dto.request.TransactionsRequest;
import id.co.bankbsi.e_walled.dto.response.PaginatedResponse;
import id.co.bankbsi.e_walled.dto.response.Response;
import id.co.bankbsi.e_walled.dto.response.TransactionResponse;
import id.co.bankbsi.e_walled.dto.response.TransactionsResponse;

import id.co.bankbsi.e_walled.exceptions.BadRequestException;
import id.co.bankbsi.e_walled.exceptions.InternalServerException;
import id.co.bankbsi.e_walled.exceptions.NotFoundException;
import id.co.bankbsi.e_walled.models.*;
import id.co.bankbsi.e_walled.repositories.*;
import id.co.bankbsi.e_walled.specifications.TransactionSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final ModelMapper modelMapper;

    @Autowired
    private final TransactionRepository transactionRepository;
    @Autowired
    private final CustomTransactionRepository customTransactionRepository;
    @Autowired
    private final TransactionCategoryRepository transactionCategoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;

    public PaginatedResponse<TransactionResponse> searchTransactionsWithPaginationSortingAndFiltering(Users userData, TransactionsRequest transactionsRequest) {

        TransactionFilterRequest filterDto = TransactionFilterRequest.builder()
                .transactionNumber(transactionsRequest.getTransactionNumber())
                .type(transactionsRequest.getType())
                .walletNumber(transactionsRequest.getWalletNumber())
                .endDate(transactionsRequest.getEndDate())
                .search(transactionsRequest.getSearch())
                .startDate(transactionsRequest.getStartDate())
                .wallet(transactionsRequest.getWallet())
                .associateWallet(transactionsRequest.getAssociateWallet())
                .categoryName(transactionsRequest.getCategoryName())
                .categoryName(transactionsRequest.getCategoryName())
                .build();

        List<SortRequest> sortRequests = jsonStringToSortDto(transactionsRequest.getSort());
        List<Sort.Order> orders = new ArrayList<>();

        if (sortRequests != null) {
            for (SortRequest sortRequest : sortRequests) {
                Sort.Direction direction = Objects.equals(sortRequest.getDirection(), "desc")
                        ? Sort.Direction.DESC : Sort.Direction.ASC;
                orders.add(new Sort.Order(direction, sortRequest.getField()));
            }
        }

        PageRequest pageRequest = PageRequest.of(
                transactionsRequest.getPage(),
                transactionsRequest.getSize(),
                Sort.by(orders)
        );

        Page<TransactionResponse> page = customTransactionRepository.findTransactionsWithSignedAmount(userData, filterDto, pageRequest);

        return new PaginatedResponse<>(page);

    }

    @Transactional
    public Response createTransactionTransfer(Users userData, CreateTransactionRequest.CreateTransactionTransferRequest req) {

        try {
            Wallets senderWallet = walletRepository.findByNumber(req.getSenderAccount());
            Wallets receiverWallet = walletRepository.findByNumber(req.getAcquirerAccount());

            validateOwnership(senderWallet, userData.getId(), "Sender");
            validateBalance(senderWallet, req.getAmount());


            Transactions transaction =  mapTransaction(req,senderWallet, receiverWallet);
            TransactionResponse transactionData = modelMapper.map(transaction, TransactionResponse.class);
            transactionData.setCategory(transaction.getCategory().getName());
            return TransactionsResponse.successCreate(transactionData);
        } catch (Exception e) {
            throw e; // Let handled exceptions bubble up
        }
    }

    private Transactions mapTransaction(CreateTransactionRequest.CreateTransactionTransferRequest req,  Wallets sender, Wallets receiver) {
        Transactions transaction = new Transactions();
        Long recordBalance = sender.getBalance() - req.getAmount();

        String description = "Transfer made from " + sender.getNumber() + " to " + receiver.getNumber();
        TransactionCategories category = transactionCategoryRepository.findById(req.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        boolean isInhouse = sender.getUser().getId() == receiver.getUser().getId();
        String txNum = generateTransactionNumber();
        System.out.println(txNum);
        transaction.setCategory(category);
        transaction.setType(TransactionTypes.TRANSFER);
        transaction.setDebit(true);
        transaction.setTransactionNumber(txNum);
        transaction.setAmount(req.getAmount());
        transaction.setAssociateWallet(receiver);
        transaction.setWallet(sender);
        transaction.setDescription(description);
        transaction.setInternal(isInhouse);
        transaction.setWalletBalanceLeft(sender.getBalance());
        transaction.setNotes(req.getNotes());

        transactionRepository.save(transaction);

        Long associateBalance = receiver.getBalance() + req.getAmount();
        Transactions transactionDebit = new Transactions();

        description = "Money received from " + sender.getNumber();

        txNum = generateTransactionNumber();
        transactionDebit.setType(TransactionTypes.TRANSFER);
        transactionDebit.setDebit(false);
        transactionDebit.setAmount(req.getAmount());
        transactionDebit.setAssociateWallet(sender);
        transactionDebit.setTransactionNumber(txNum);
        transactionDebit.setWallet(receiver);
        transactionDebit.setDescription(description);
        transactionDebit.setInternal(isInhouse);
        transactionDebit.setWalletBalanceLeft(receiver.getBalance());
        transactionDebit.setNotes(req.getNotes());

        transactionRepository.save(transactionDebit);

        walletRepository.updateWalletBalance(sender.getId(), recordBalance);
        walletRepository.updateWalletBalance(receiver.getId(), associateBalance);

        return transaction;
    }

    @Transactional
    public Response createTransactionTopUp(Users userData, CreateTransactionRequest.CreateTransactionTopUpRequest req) {

        try {
            Transactions transaction = modelMapper.map(req, Transactions.class);

            Wallets wallets = walletRepository.findByNumber(req.getAcquirerAccount());

            validateOwnership(wallets, userData.getId(), "User wallet");


            String txNum = generateTransactionNumber();
            Long acquirerBalance = wallets.getBalance() + req.getAmount();
            String description = "Top up using "+req.getVia();
            transaction.setTransactionNumber(generateTransactionNumber());
            transaction.setType(TransactionTypes.TOPUP);
            transaction.setWalletBalanceLeft(acquirerBalance);
            transaction.setType(TransactionTypes.TOPUP);
            transaction.setDebit(false);
            transaction.setAmount(req.getAmount());
            transaction.setTransactionNumber(txNum);
            transaction.setWallet(wallets);
            transaction.setDescription(description);
            transaction.setInternal(false);
            transaction.setNotes(req.getNotes());

            transactionRepository.save(transaction);
            walletRepository.updateWalletBalance(wallets.getId(), acquirerBalance);
            TransactionResponse transactionData = modelMapper.map(transaction, TransactionResponse.class);
            return TransactionsResponse.successCreate(transactionData);
        } catch (Exception e) {
            throw e; // Let handled exceptions bubble up
        }
    }


//    @Transactional
//    public List<TransactionDTO> getTransactionsForStatement(UUID walletId) {
//        // Fetch transactions for a particular wallet (associateWallet or wallet)
//        List<Transactions> transactions = .findByassociateWalletIdOrwalletId(walletId, walletId);
//
//        double balance = 0; // Initial balance, for example
//        return transactions.stream()
//                .map(transaction -> {
//                    // Here, you can customize how you generate the description and balance after
//                    balance += transaction.getAmount(); // Adjust as per your business logic
//                    return new TransactionDTO(
//                            transaction.getCreatedAt().toString(),  // Assuming createdAt is the transaction date
//                            transaction.getNotes(),
//                            transaction.getType().name(),
//                            transaction.getAmount() / 100.0, // Assuming amount is stored in cents
//                            balance / 100.0  // Assuming amount is in cents, adjust as needed
//                    );
//                })
//                .collect(Collectors.toList());
//    }

    private void validateOwnership(Wallets sender, UUID userId, String ownership) {
        if (sender == null || sender.getUser() == null || !sender.getUser().getId().equals(userId)) {
            throw new BadRequestException(ownership + " account doesn't belong to the logged-in user");
        }
    }

    private void validateBalance(Wallets sender, Long amount) {
        if (amount > sender.getBalance()) {
            throw new BadRequestException("Insufficient balance");
        }
    }

    public String generateTransactionNumber() {
        String currentDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String transactionSuffix = generateTransactionSuffixForDay(currentDate);
        return "TXN-" + currentDate + "-" + transactionSuffix;
    }

    private String generateTransactionSuffixForDay(String currentDate) {
        String latestTxnNumber = transactionRepository.findLatestTransactionNumberForDate(currentDate);

        int nextSequence = 1;

        if (latestTxnNumber != null) {
            String[] parts = latestTxnNumber.split("-");
            String lastSuffix = parts[2];
            nextSequence = Integer.parseInt(lastSuffix) + 1;
        }

        return String.format("%04d", nextSequence);
    }

    private List<SortRequest> jsonStringToSortDto(String jsonString) {
        try {
            ObjectMapper obj = new ObjectMapper();
            return obj.readValue(jsonString, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }
}
