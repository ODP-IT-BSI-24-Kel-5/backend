package id.co.bankbsi.e_walled.controllers;


import id.co.bankbsi.e_walled.dto.response.Response;
import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<Response> myProfile(@AuthenticationPrincipal Users user) {
        Response res = userService.getProfile(user);
        return ResponseEntity.status(res.getCode()).body(res);
    }
}
