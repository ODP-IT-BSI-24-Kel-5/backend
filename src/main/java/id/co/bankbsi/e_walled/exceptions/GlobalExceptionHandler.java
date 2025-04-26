package id.co.bankbsi.e_walled.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException ex) {
        return buildResponse("Token has expired", ex.getMessage(), HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleSignatureException(SignatureException ex) {
        return buildResponse("Invalid token signature", ex.getMessage(), HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<?> handleMalformedJwtException(MalformedJwtException ex) {
        return buildResponse("Malformed JWT token", ex.getMessage(), HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return buildResponse("Malformed request body or URL", ex.getMessage(), HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing // in case of duplicate fields
                ));

        Map<String, Object> body = new HashMap<>();
        body.put("status", "failed");
        body.put("message", errors);

//        logger.warn("Validation failed: {}", errorMessages);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException ex) {
        return buildResponse("Resource not found", ex.getMessage(), HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<?> handleInternalServerException(InternalServerException ex) {
        return buildResponse("Internal server error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException ex) {
        return buildResponse("Bad request", ex.getMessage(), HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        return buildResponse("Invalid request parameters", ex.getMessage(), HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> error = new HashMap<>();
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            error.put("message", String.format("Invalid value '%s' for type. Allowed values: %s",
                    ex.getValue(),
                    Arrays.toString(ex.getRequiredType().getEnumConstants())));
            error.put("success", "failed");
        } else {
            error.put("error", "Invalid parameter: " + ex.getName());
        }
        logger.warn("Type mismatch: {}", error);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        return buildResponse("Missing parameter", ex.getParameterName() + " is missing", HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return buildResponse("Method not allowed", ex.getMessage(), HttpStatus.METHOD_NOT_ALLOWED, ex);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return buildResponse("Unsupported media type", ex.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<?> handleMissingPathVariable(MissingPathVariableException ex) {
        return buildResponse("Missing path variable", ex.getVariableName() + " is missing", HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(org.springframework.core.convert.ConversionFailedException.class)
    public ResponseEntity<?> handleConversionFailed(org.springframework.core.convert.ConversionFailedException ex) {
        return buildResponse("Conversion failed", ex.getMessage(), HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        return buildResponse("Data integrity violation", ex.getRootCause().getMessage(), HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNoHandlerFound(NoHandlerFoundException ex) {
        return buildResponse("No handler found", ex.getMessage(), HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllUncaughtException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        return buildResponse("Unexpected server error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    private ResponseEntity<ErrorResponse> buildResponse(String message, String details, HttpStatus status, Exception ex) {
        logger.warn("Handled exception: {} - {}", message, ex.getMessage());
        ErrorResponse res = ErrorResponse.setErrMesssage(message, details);
        return new ResponseEntity<>(res, status);
    }
}


@Data
@AllArgsConstructor
class ErrorResponse {
    private String status = "failed";
    private String message;
    private String error;

    public ErrorResponse(String message, String error) {
        this.message = message;
        this.error = error;
    }

    public static ErrorResponse setErrMesssage(String message, String error) {
        return new ErrorResponse(message, error);
    }
}