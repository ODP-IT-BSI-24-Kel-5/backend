package id.co.bankbsi.e_walled.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class LoginResponse extends Response{
    private String token;

    public LoginResponse(String status, String message, HttpStatus statusCode, String token) {
        super(status, message, statusCode);
        this.token = token;
    }

    public static LoginResponse success(String message, String token) {
        return new LoginResponse("success", message, HttpStatus.OK, token);
    }
}
