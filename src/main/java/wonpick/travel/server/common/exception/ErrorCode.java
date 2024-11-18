package wonpick.travel.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common Errors
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "Invalid input value"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "Internal server error"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "Resource not found"),

    // Authentication/Authorization Errors
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "Authentication is required"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "A002", "Access denied"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "Invalid token"),

    // Payment Related Errors
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "P001", "결제 승인 실패"),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "P002", "결제 금액이 올바르지 않습니다"),
    PAYMENT_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "P003", "Payment timeout"),

    // Reservation Related Errors
    SEAT_NOT_AVAILABLE(HttpStatus.CONFLICT, "R001", "Requested seat is not available"),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R002", "Reservation not found"),
    INVALID_FLIGHT(HttpStatus.BAD_REQUEST, "R003", "Invalid flight information"),

    // Lock Related Errors
    LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, "L001", "Lock 획득에 실패했습니다."),
    LOCK_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "L002", "Lock 획득 TIMEOUT");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
