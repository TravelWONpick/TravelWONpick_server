package wonpick.travel.server.common.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String message;
    private final Object data;

    public BaseException(ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage(), null);
    }

    public BaseException(ErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }

    public BaseException(ErrorCode errorCode, Object data) {
        this(errorCode, errorCode.getMessage(), data);
    }

    public BaseException(ErrorCode errorCode, String message, Object data) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
    }
}



