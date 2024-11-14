package wonpick.travel.server.dto;


import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostPaymentConfirmRequest {
    private String orderId;
    private Integer amount;
    private String paymentKey;
}