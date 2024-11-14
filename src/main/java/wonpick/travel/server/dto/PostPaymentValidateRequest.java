package wonpick.travel.server.dto;

import lombok.Data;

@Data
public class PostPaymentValidateRequest {
    private String orderId;
    private Integer amount;
}
