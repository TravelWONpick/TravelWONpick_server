package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationFlightResponseDTO {
    private String flightNumber;
    private String destination;
    private String departureTime;
    private String arrivalTime;
}
