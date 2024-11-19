package wonpick.travel.server.entity;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id", nullable = false)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_seq_id", nullable = false)
    private Order order;

    @Column(name = "buy_date", nullable = false)
    private LocalDateTime buyDate;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @Column(nullable = false)
    private String journey;

    @Column(name = "seat_count", nullable = false)
    private Long seatCount;

    @Column(name = "boarding_date", nullable = false)
    private LocalDateTime boardingDate;

    // ReservationFlight와 일대다 관계
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationFlight> reservationFlights;
}


