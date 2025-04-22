package id.co.bankbsi.e_walled.controllers;

import id.co.bankbsi.e_walled.dto.request.CreatePinRequest;
import id.co.bankbsi.e_walled.dto.request.LoginRequest;
import id.co.bankbsi.e_walled.dto.request.RegisterRequest;
import id.co.bankbsi.e_walled.dto.response.Response;
import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final AuthService authServices;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody @Valid RegisterRequest req) {
        Response res = authServices.registerUser(req);
        return ResponseEntity.status(res.getCode()).body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<Response> returnHistory(@RequestBody @Valid LoginRequest req) {
        Response res = authServices.loginUser(req);
        return ResponseEntity.status(res.getCode()).body(res);
    }

    @PostMapping("/pins")
    public ResponseEntity<Response> createPin(@AuthenticationPrincipal Users user, @RequestBody @Valid CreatePinRequest req) {
        Response res = authServices.setPin(user, req);
        return ResponseEntity.status(res.getCode()).body(res);
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logout(@AuthenticationPrincipal Users user) {
        Response res = authServices.logoutUser(user);
        return ResponseEntity.status(res.getCode()).body(res);
    }
}
