package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationFlightDTO {
    private Long seatCount; // reservation 테이블의 seatCount
    private long originPrice; // flight 테이블의 originPrice X seatCount
    private long discount; // amount - originPrice
    private long amount; // orders 테이블의 amount

    private String outFlightNumber; // 가는편 flightNumber
    private String outJourney; // 가는편 여정
    private String outDepartureTime; // 가는편 출발시간
    private String outArrivalTime; // 가는편 도착시간

    private String inFlightNumber; // 오는편 flightNumber
    private String inJourney; // 오는편 여정
    private String inDepartureTime; // 오는편 출발시간
    private String inArrivalTime; // 오는편 도착시간
}
