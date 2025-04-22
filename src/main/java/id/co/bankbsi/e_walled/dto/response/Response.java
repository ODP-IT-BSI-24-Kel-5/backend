package id.co.bankbsi.e_walled.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;

@Data
@Accessors(chain = true)
@Scope("prototype")
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private String status = "success";
    private String message = "";

    @JsonIgnore
    private HttpStatus code;

    public static Response success(String message) {
        return new Response("success", message, HttpStatus.OK);
    }
    public static Response successCreated(String message) {
        return new Response("success", message, HttpStatus.CREATED);
    }

    public static Response failedServer(String message) {
        return new Response("failed", message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public static Response failedRequest(String message) {
        return new Response("failed", message, HttpStatus.BAD_REQUEST);
    }
    public static Response failedNotFound(String message) {
        return new Response("failed", message, HttpStatus.NOT_FOUND);
    }
}
