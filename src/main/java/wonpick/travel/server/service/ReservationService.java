package wonpick.travel.server.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import wonpick.travel.server.dto.PostPaymentConfirmRequest;
import wonpick.travel.server.dto.PostPaymentConfirmResponse;
import wonpick.travel.server.entity.Flight;
import wonpick.travel.server.entity.Order;
import wonpick.travel.server.entity.Reservation;
import wonpick.travel.server.entity.ReservationFlight;
import wonpick.travel.server.repository.ReservationFlightRepository;
import wonpick.travel.server.repository.ReservationRepository;
import wonpick.travel.server.util.DateUtil;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final Logger logger = LogManager.getLogger(ReservationService.class);

    private final RedissonClient redissonClient;
    private final ReservationRepository reservationRepository;
    private final ReservationFlightRepository reservationFlightRepository;
    private final OrderService orderService;
    private final FlightService flightService;


    public Reservation createReservation(PostPaymentConfirmRequest request, PostPaymentConfirmResponse response,
                                         Long outboundFlightId, Long inboundFlightId, Long seatCount) {

        // Order 조회 (존재하지 않을 경우 예외 처리)
        Order order = orderService.findByOrderId(request.getOrderId());

        // Flight 정보 조회 (FlightService를 통해 조회)
        Flight outboundFlight = flightService.findFlightById(outboundFlightId);
        Flight inboundFlight = flightService.findFlightById(inboundFlightId);

        // journey 생성
        String journey = String.format("%s (%s) -> %s (%s)",
                outboundFlight.getDeparturePlace(), outboundFlight.getDepartureAirportCode(),
                outboundFlight.getArrivalPlace(), outboundFlight.getArrivalAirportCode());

        // Reservation 생성
        Reservation reservation = Reservation.builder()
                .order(order)
                .buyDate(DateUtil.convertToLocalDateTime(response.getApprovedAt()))
                .totalAmount(response.getTotalAmount())
                .journey(journey)
                .seatCount(seatCount)
                .boardingDate(outboundFlight.getDepartureTime())
                .build();


        // Reservation 저장
        reservation = reservationRepository.save(reservation);

        // 항공권 잔여석 차감
        flightService.adjustFlightSeatCount(outboundFlightId, seatCount);
        flightService.adjustFlightSeatCount(inboundFlightId, seatCount);

        // ReservationFlight 생성 및 저장
        createReservationFlight(reservation, outboundFlight);
        createReservationFlight(reservation, inboundFlight);

        return reservation;

    }

    private void createReservationFlight(Reservation reservation, Flight flight) {
        ReservationFlight reservationFlight =
                ReservationFlight.builder()
                        .reservation(reservation)
                        .flight(flight)
                        .build();

        reservationFlightRepository.save(reservationFlight);
    }

}
