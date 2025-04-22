package id.co.bankbsi.e_walled.controllers;


import id.co.bankbsi.e_walled.dto.response.Response;
import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.repositories.TransactionCategoryRepository;
import id.co.bankbsi.e_walled.services.TransactionCategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/transactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TransactionCategoryController {

    @Autowired
    private final TransactionCategoryService transactionCategoryService;

    @GetMapping("/categories")
    public ResponseEntity<?> getWallet(@AuthenticationPrincipal Users user) {
        Response res = transactionCategoryService.getTransactionCategory();
        return ResponseEntity.status(200).body(res);
    }
}
