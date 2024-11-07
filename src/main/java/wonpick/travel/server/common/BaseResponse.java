package wonpick.travel.server.dto;

import org.springframework.http.HttpStatus;

public class BaseResponse<T> {
    private int status;
    private String message;
    private T data;

    public BaseResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(HttpStatus.OK.value(), "Success", data);
    }

    public static <T> BaseResponse<T> failure(String message) {
        return new BaseResponse<>(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    // Getters and Setters
    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
