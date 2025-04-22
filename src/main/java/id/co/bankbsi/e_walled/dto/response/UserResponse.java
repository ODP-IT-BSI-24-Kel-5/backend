package id.co.bankbsi.e_walled.dto.response;

import id.co.bankbsi.e_walled.models.Users;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class UserResponse extends Response {
    private Users users;

    public UserResponse(String status, String message, HttpStatus statusCode, Users users) {
        super(status, message, statusCode);
        this.users = users;
    }

    public static UserResponse success(Users users) {
        return new UserResponse("success", "User retrieved successfully!", HttpStatus.OK, users);
    }

    public static UserResponse successCreate(Users users) {
        return new UserResponse("success", "Wallet created successfully!", HttpStatus.CREATED, users);
    }
}