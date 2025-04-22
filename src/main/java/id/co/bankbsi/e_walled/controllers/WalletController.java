package id.co.bankbsi.e_walled.controllers;

import id.co.bankbsi.e_walled.dto.request.CreateWalletRequest;
import id.co.bankbsi.e_walled.dto.request.UpdateWalletRequest;
import id.co.bankbsi.e_walled.dto.response.Response;
import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.services.WalletService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users/wallets")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WalletController {
    private final WalletService walletService;

    @GetMapping("")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Response> getWallet(@AuthenticationPrincipal Users user) {
        Response res = walletService.getWallet(user);
        return ResponseEntity.status(res.getCode()).body(res);
    }

    @PostMapping("")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Response> createWallet(@AuthenticationPrincipal Users user, @RequestBody @Valid CreateWalletRequest createWalletRequest) {
        Response res = walletService.createWallet(user,createWalletRequest);
        return ResponseEntity.status(res.getCode()).body(res);
    }

    @PutMapping("/{wallet}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Response> updateWallet(@AuthenticationPrincipal Users user, @PathVariable String wallet ,@RequestBody @Valid UpdateWalletRequest updateWalletRequest) {
        Response res = walletService.updateWallet(user, wallet, updateWalletRequest);
        return ResponseEntity.status(res.getCode()).body(res);
    }

    @GetMapping("/main")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Response> getMainWallet(@AuthenticationPrincipal Users user) {
        Response res = walletService.getMainWallet(user);
        return ResponseEntity.status(res.getCode()).body(res);
    }

    @GetMapping("/{number}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Response> getSpecificWallet(@PathVariable(value = "number") String number) {
        Response res = walletService.getSpecificWalletGeneral(number);
        return ResponseEntity.status(res.getCode()).body(res);
    }

    @GetMapping("/{number}/detail")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Response> getSpecificWalletDetail(@AuthenticationPrincipal Users user, @PathVariable(value = "number") String number) {
        Response res = walletService.getSpecificWallet(user, number);
        return ResponseEntity.status(res.getCode()).body(res);
    }
}
