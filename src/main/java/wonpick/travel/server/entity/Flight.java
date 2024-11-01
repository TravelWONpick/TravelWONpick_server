package wonpick.travel.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Flight extends BaseEntity {

    @Id
    @Column(name = "flight_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    // SpecialPricePick 엔티티와의 다대일 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sp_id", nullable = false)
    private SpecialPricePick specialPricePick;

    @Column(name = "airline", nullable = false, length = 20)
    private String airline;

    @Column(name = "flight_number", nullable = false, length = 20)
    private String flightNumber;

    @Column(name = "departure_place", nullable = false, length = 50)
    private String departurePlace;

    @Column(name = "arrival_place", nullable = false, length = 50)
    private String arrivalPlace;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @Column(name = "max_seat", nullable = false)
    private int maxSeat;

    @Column(name = "origin_price", nullable = false)
    private int originPrice;

    @Column(name = "special_price", nullable = false)
    private int specialPrice;

    @Column(name = "departure_airport_code", nullable = false, length = 20)
    private String departureAirportCode;

    @Column(name = "arrival_airport_code", nullable = false, length = 20)
    private String arrivalAirportCode;

    @Column(name = "baggage", nullable = false, length = 20)
    private String baggage;

    // ReservationFlight와 일대다 관계
    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationFlight> reservationFlights;

}