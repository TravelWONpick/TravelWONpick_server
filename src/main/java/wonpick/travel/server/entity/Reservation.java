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
@Builder
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id", nullable = false)
    private Long id;

    @Column(name = "order_id", nullable = false, length = 6)
    private String orderId; // 마이페이지 예약번호

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_seq_id", nullable = false)
    private Order order;

    @Column(name = "is_round_trip", nullable = false)
    private Boolean isRoundTrip;

    @Column(name = "buy_date", nullable = false)
    private LocalDateTime buyDate;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    // ReservationFlight와 일대다 관계
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationFlight> reservationFlights;
}


