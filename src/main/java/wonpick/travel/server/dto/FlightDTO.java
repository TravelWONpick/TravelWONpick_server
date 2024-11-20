package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class FlightDTO {
    private Long flightId;
    private String airline;
    private String flightNumber;
    private String departurePlace;
    private String arrivalPlace;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int specialPrice;
    private String departureAirportCode;
    private String arrivalAirportCode;
    private String baggage;
    private Long maxSeat;
}