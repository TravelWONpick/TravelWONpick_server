package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationDTO {
    private String orderId;
    private String boardingDate;
    private String journey;
    private Long seatCount;
}
