package id.co.bankbsi.e_walled.services;

import id.co.bankbsi.e_walled.dto.response.Response;
import id.co.bankbsi.e_walled.dto.response.TransactionsResponse;
import id.co.bankbsi.e_walled.dto.response.UserResponse;
import id.co.bankbsi.e_walled.exceptions.BadRequestException;
import id.co.bankbsi.e_walled.exceptions.InternalServerException;
import id.co.bankbsi.e_walled.exceptions.NotFoundException;
import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public Response getProfile(Users user) {
        return UserResponse.success(user);
    }
}
