package id.co.bankbsi.e_walled.services;

import id.co.bankbsi.e_walled.dto.request.CreateWalletRequest;
import id.co.bankbsi.e_walled.dto.request.LoginRequest;
import id.co.bankbsi.e_walled.dto.request.RegisterRequest;
import id.co.bankbsi.e_walled.dto.response.LoginResponse;
import id.co.bankbsi.e_walled.dto.response.Response;
import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.repositories.UserRepository;
import id.co.bankbsi.e_walled.utils.JWTTokenUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService;
    private final JWTTokenUtils jwtUtil;

    @Autowired
    private final UserRepository userRepository;

    @Transactional
    public Response registerUser(RegisterRequest dto) {
        try {
            Optional<Users> existingUser = userRepository.findFirstByEmailOrMobilePhoneOrFullName(
                    dto.getEmail(), dto.getMobilePhone(), dto.getFullName()
            );

            if (existingUser.isPresent()) {
                Users user = existingUser.get();

                if (user.getEmail().equals(dto.getEmail())) {
                    return Response.failedRequest("Email already registered");
                } else if (user.getMobilePhone().equals(dto.getMobilePhone())) {
                    return Response.failedRequest("Mobile phone already registered");
                } else if (user.getFullName().equals(dto.getFullName())) {
                    return Response.failedRequest("Full name already registered");
                }
            }
            // Map DTO to entity
            Users user = modelMapper.map(dto, Users.class);

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            Users savedUser = userRepository.save(user);
            walletService.insertWallet(savedUser, new CreateWalletRequest().setMain(true), true);

            return Response.successCreated("Register success!");

        } catch (Exception e) {
            return Response.failedServer("Registration failed: " + e.getMessage());
        }
    }


    public Response loginUser(LoginRequest dto) {
        try {
            Users user = userRepository.findFirstByEmail(dto.getEmail());

            if (user == null) {
                return Response.failedNotFound("User not found");
            }

            if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                return Response.failedRequest("Invalid credentials");
            }

            user.setSessionId(UUID.randomUUID());
            userRepository.save(user);

            String token = jwtUtil.generateToken(user);

            return LoginResponse.success("Login successful", token);
        } catch (Exception e) {
            return Response.failedServer("Login failed: " + e.getMessage());
        }
    }

    public Response logoutUser(Users user) {
        try {
            user.setTokenVersion(user.getTokenVersion() + 1);
            user.setSessionId(null); // You can also generate a new UUID here if you prefer

            userRepository.save(user);
            return Response.success("Logout successful");
        } catch (Exception e) {
            return Response.failedServer("Logout failed: " + e.getMessage());
        }
    }
}
