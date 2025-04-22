package id.co.bankbsi.e_walled.controllers;


import id.co.bankbsi.e_walled.dto.response.Response;
import id.co.bankbsi.e_walled.dto.response.UserResponse;
import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/profile")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<Response> myProfile(@AuthenticationPrincipal Users user) {
        Response res = userService.getProfile(user);
        return ResponseEntity.status(res.getCode()).body(res);
    }

    @PostMapping("/update")
    public ResponseEntity<Response> updateProfile(@AuthenticationPrincipal Users user,
                                                  @RequestParam(value = "name", required = false) String name,
                                                  @RequestParam(value = "email", required = false) String email,
                                                  @RequestParam(value = "mobile_phone", required = false) String mobilePhone,
                                                  @RequestParam(value = "image_url", required = false) MultipartFile imageFile) {
        try {
            Response res = userService.updateUserProfile(user, name, email, mobilePhone, imageFile);
            return ResponseEntity.status(res.getCode()).body(res);
        } catch (IOException e) {
            e.printStackTrace();
            Response res = UserResponse.failedRequest("Error updating user profile: " + e.getMessage());
            return ResponseEntity.status(res.getCode()).body(res);
        }
    }

}
