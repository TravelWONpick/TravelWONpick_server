package wonpick.travel.server.dto;

import lombok.Data;

@Data
public class PostOrderRequest {
    private String orderId;
    private Integer amount;
}
