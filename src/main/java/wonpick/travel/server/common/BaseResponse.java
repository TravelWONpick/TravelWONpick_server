package wonpick.travel.server.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseResponse<T> {
    // Getters and Setters
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


    public static <T> BaseResponse<T> failure(String message, HttpStatus status) {
        return new BaseResponse<>(status.value(), message, null);
    }

}
