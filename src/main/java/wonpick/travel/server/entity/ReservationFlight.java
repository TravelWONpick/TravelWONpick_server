package wonpick.travel.server.entity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationFlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rf_id", nullable = false)
    private Long id;

    // ManyToOne 관계로 Reservation 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    // ManyToOne 관계로 Flight 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;
}
