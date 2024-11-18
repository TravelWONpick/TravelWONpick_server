package wonpick.travel.server.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        log.error("HandleBaseException: {}", e.getMessage(), e);
        return createErrorResponse(e.getErrorCode(), e.getMessage(), e.getData());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("HandleMethodArgumentNotValidException: {}", e.getMessage(), e);
        List<FieldError> fieldErrors = getFieldErrors(e);
        return createErrorResponse(ErrorCode.INVALID_INPUT_VALUE, "Invalid input value", fieldErrors);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("HandleException: {}", e.getMessage(), e);
        return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), null);
    }

    private List<FieldError> getFieldErrors(BindException e) {
        return e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldError(
                        error.getField(),
                        error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                        error.getDefaultMessage()))
                .collect(Collectors.toList());
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode, String message, Object data) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .code(errorCode.getCode())
                        .message(message)
                        .data(data)
                        .build(),
                errorCode.getHttpStatus());
    }
}
