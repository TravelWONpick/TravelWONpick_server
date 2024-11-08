package wonpick.travel.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wonpick.travel.server.dto.FlightDTO;
import wonpick.travel.server.entity.Flight;
import wonpick.travel.server.repository.FlightRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    public List<FlightDTO> getFlightsBySpecialPriceId(Long spId) {
        List<Flight> flights = flightRepository.findBySpecialPriceId(spId);

        return flights.stream()
                .map(flight -> new FlightDTO(
                        flight.getId(),
                        flight.getSpecialPricePick().getId(),
                        flight.getAirline(),
                        flight.getFlightNumber(),
                        flight.getDeparturePlace(),
                        flight.getArrivalPlace(),
                        flight.getDepartureTime(),
                        flight.getArrivalTime(),
                        flight.getMaxSeat(),
                        flight.getOriginPrice(),
                        flight.getSpecialPrice(),
                        flight.getDepartureAirportCode(),
                        flight.getArrivalAirportCode(),
                        flight.getBaggage()
                ))
                .collect(Collectors.toList());
    }
}