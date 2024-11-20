package wonpick.travel.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wonpick.travel.server.dto.FlightDTO;
import wonpick.travel.server.entity.Flight;
import wonpick.travel.server.repository.FlightRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;


    public Flight findFlightById(Long flightId) {
        return flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("항공편 정보를 찾을 수 없습니다."));
    }

    public List<FlightDTO> searchFlights(Long spId, String depAirportCode, String arrAirportCode, String departureDate) {

        LocalDate date = LocalDate.parse(departureDate);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return flightRepository.findFlightsByDateAndLocation(spId, depAirportCode, arrAirportCode, startOfDay, endOfDay)
                .stream().map(flight ->
                        FlightDTO.builder()
                                .flightId(flight.getId())
                                .airline(flight.getAirline())
                                .flightNumber(flight.getFlightNumber())
                                .departurePlace(flight.getDeparturePlace())
                                .arrivalPlace(flight.getArrivalPlace())
                                .departureTime(flight.getDepartureTime())
                                .arrivalTime(flight.getArrivalTime())
                                .specialPrice(flight.getSpecialPrice())
                                .departureAirportCode(flight.getDepartureAirportCode())
                                .arrivalAirportCode(flight.getArrivalAirportCode())
                                .baggage(flight.getBaggage())
                                .maxSeat(flight.getMaxSeat())
                                .build()

                )
                .collect(Collectors.toList());

    }

    public void adjustFlightSeatCount(Long flightId, Long seatCount) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("해당 항공편을 찾을 수 없습니다."));

        if (flight.getMaxSeat() >= seatCount) {
            flight.setMaxSeat(flight.getMaxSeat() - seatCount);
            flightRepository.save(flight);
        } else {
            throw new RuntimeException("잔여 좌석이 부족합니다.");
        }
    }

}