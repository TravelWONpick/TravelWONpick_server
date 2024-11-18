package wonpick.travel.server.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldError {
    private String field;
    private String value;
    private String reason;
}