package wonpick.travel.server.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


// TODO: Toss에서 제공하는 Payment 객체와 요청으로 들어갈 Response 객체로 분리해야 한다.
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostPaymentConfirmResponse {
    @JsonProperty("approvedAt")
    private String approvedAt;

    @JsonProperty("totalAmount")
    private Integer totalAmount;
}