package wonpick.travel.server.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PostPaymentValidateResponse {
    private Boolean valid;
    private String message;
}
