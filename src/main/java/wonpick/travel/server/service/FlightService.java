package wonpick.travel.server.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import wonpick.travel.server.dto.FlightDTO;
import wonpick.travel.server.entity.Flight;
import wonpick.travel.server.repository.FlightRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightService {
    private static final int LOCK_WAIT_TIME = 10;
    private static final int LOCK_LEASE_TIME = 5;

    private final FlightRepository flightRepository;
    private final RedissonClient redissonClient;


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
                                .build()

                )
                .collect(Collectors.toList());

    }


    public void adjustFlightSeatCountWithLock(Long flightId, Long seatCount) {
        RLock lock = redissonClient.getLock("flight:seats:" + flightId);

        try {
            if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
                Flight flight = flightRepository.findById(flightId)
                        .orElseThrow(() -> new RuntimeException("해당 항공편을 찾을 수 없습니다."));

                if (flight.getMaxSeat() >= seatCount) {
                    flight.setMaxSeat(flight.getMaxSeat() - seatCount);
                    flightRepository.save(flight);
                } else {
                    throw new RuntimeException("잔여 좌석이 부족합니다.");
                }
            } else {
                throw new RuntimeException("좌석 차감에 대한 락을 획득할 수 없습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("좌석 차감 과정에서 오류가 발생했습니다.", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}