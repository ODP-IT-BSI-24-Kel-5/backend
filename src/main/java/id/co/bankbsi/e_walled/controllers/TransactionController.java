package id.co.bankbsi.e_walled.controllers;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import id.co.bankbsi.e_walled.annotations.ValidEnum;
import id.co.bankbsi.e_walled.dto.request.CreateTransactionRequest;
import id.co.bankbsi.e_walled.dto.request.TransactionsRequest;
import id.co.bankbsi.e_walled.dto.response.PaginatedResponse;
import id.co.bankbsi.e_walled.dto.response.Response;
import id.co.bankbsi.e_walled.dto.response.TransactionResponse;

import id.co.bankbsi.e_walled.models.TransactionTypes;
import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.services.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/transactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    @Autowired
    private final TransactionService transactionService;

    @GetMapping("/{wallet}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PaginatedResponse<TransactionResponse>> getTransaction(@AuthenticationPrincipal Users userData,
                                                                                 @RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                                 @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                                                 @RequestParam(name = "sort", defaultValue = "id") String sort,
                                                                                 @RequestParam(name = "direction", defaultValue = "asc") String direction,
                                                                                 @RequestParam(name = "start_date", required = false) String startDate,
                                                                                 @RequestParam(name = "type", required = false) @ValidEnum(enumClass = TransactionTypes.class) TransactionTypes type,
                                                                                 @RequestParam(name = "category_name", required = false) String categoryName,
                                                                                 @RequestParam(name = "transaction_number", required = false) String transactionNumber,
                                                                                 @RequestParam(name = "acquirer_number", required = false) String associateWallet,
                                                                                 @RequestParam(name = "sender_number", required = false) String wallet,
                                                                                 @RequestParam(name = "end_date", required = false) String endDate,
                                                                                 @RequestParam(name = "search", required = false) String search,
                                                                                 @PathVariable(name = "wallet") String walletNumber) {

        int adjustedPage = Math.max(0, page - 1);
        PaginatedResponse<TransactionResponse> transactions = transactionService.searchTransactionsWithPaginationSortingAndFiltering(userData,
                TransactionsRequest.builder()
                        .transactionNumber(transactionNumber)
                        .associateWallet(associateWallet)
                        .wallet(wallet)
                        .walletNumber(walletNumber)
                        .startDate(startDate)
                        .endDate(endDate)
                        .search(search)
                        .type(type)
                        .categoryName(categoryName)
                        .page(adjustedPage)
                        .size(size)
                        .sort(sort)
                        .direction(direction)
                        .build());

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<PaginatedResponse<TransactionResponse>> getAllTransaction(@AuthenticationPrincipal Users userData,
                                                                                    @RequestParam(name = "page", defaultValue = "1") Integer page,
                                                                                    @RequestParam(name = "size", defaultValue = "5") Integer size,
                                                                                    @RequestParam(name = "sort", defaultValue = "id") String sort,
                                                                                    @RequestParam(name = "direction", defaultValue = "asc") String direction,
                                                                                    @RequestParam(name = "start_date", required = false) String startDate,
                                                                                    @RequestParam(name = "type", required = false) @ValidEnum(enumClass = TransactionTypes.class) TransactionTypes type,
                                                                                    @RequestParam(name = "category_name", required = false) String categoryName,
                                                                                    @RequestParam(name = "transaction_number", required = false) String transactionNumber,
                                                                                    @RequestParam(name = "acquirer_number", required = false) String associateWallet,
                                                                                    @RequestParam(name = "sender_number", required = false) String wallet,
                                                                                    @RequestParam(name = "end_date", required = false) String endDate,
                                                                                    @RequestParam(name = "search", required = false) String search,
                                                                                    @RequestParam(name = "wallet", required = false) String walletNumber) {


        int adjustedPage = Math.max(0, page - 1);
        PaginatedResponse<TransactionResponse> transactions = transactionService.searchTransactionsWithPaginationSortingAndFiltering(userData,
                TransactionsRequest.builder()
                        .transactionNumber(transactionNumber)
                        .associateWallet(associateWallet)
                        .wallet(wallet)
                        .walletNumber(walletNumber)
                        .startDate(startDate)
                        .endDate(endDate)
                        .search(search)
                        .type(type)
                        .categoryName(categoryName)
                        .page(adjustedPage)
                        .size(size)
                        .sort(sort)
                        .direction(direction)
                        .build());

        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/transfer")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Response> createTransactionTransfer(@AuthenticationPrincipal Users userData, @RequestBody @Valid CreateTransactionRequest.CreateTransactionTransferRequest req) {
        Response res = transactionService.createTransactionTransfer(userData, req);
        return ResponseEntity.status(res.getCode()).body(res);
    }

    @PostMapping("/topup")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Response> createTransactionTransfer(@AuthenticationPrincipal Users userData, @RequestBody @Valid CreateTransactionRequest.CreateTransactionTopUpRequest req) {
        Response res = transactionService.createTransactionTopUp(userData, req);
        return ResponseEntity.status(res.getCode()).body(res);
    }
}
