package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RoundTripFlightResponse {

    private List<FlightDTO> outboundFlights;  // 가는 항공편 리스트
    private List<FlightDTO> returnFlights;    // 오는 항공편 리스트

}
