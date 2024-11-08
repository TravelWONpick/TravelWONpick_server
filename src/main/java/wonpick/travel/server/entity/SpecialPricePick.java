package wonpick.travel.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SpecialPricePick extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sp_id", nullable = false)
    private Long id;

    @Column(name = "destination", nullable = false, length = 20)
    private String destination;

    @Column(name = "departure_date", nullable = false, length = 20)
    private String departureDate;

    @Column(name = "title", nullable = false, length = 20)
    private String title;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @Column(name = "open_time", nullable = false)
    private LocalDateTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalDateTime closeTime;

    @Column(name = "category", nullable = false, length = 20)
    private String category;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name="departure_airport_code", nullable = false, length=20)
    private String depAirportCode;

    @Column(name="arrival_airport_code", nullable = false, length=20)
    private String arrAirportCode;

    // Flight 엔티티와의 일대다 관계 설정
    @OneToMany(mappedBy = "specialPricePick", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Flight> flights;
}
