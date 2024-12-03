package wonpick.travel.server.dto;


import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
    public class PostPaymentConfirmRequest {
        private String orderId;
        private Integer amount;
        private String paymentKey;
        private Long depFlightId;
        private Long arrFlightId;
        private Long seatCount;
        private List<FlightPassengerDTO> passengers;
    }