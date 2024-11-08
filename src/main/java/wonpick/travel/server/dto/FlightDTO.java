package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FlightDTO {
    private Long id;
    private Long specialPricePickId;
    private String airline;
    private String flightNumber;
    private String departurePlace;
    private String arrivalPlace;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int maxSeat;
    private int originPrice;
    private int specialPrice;
    private String departureAirportCode;
    private String arrivalAirportCode;
    private String baggage;
}