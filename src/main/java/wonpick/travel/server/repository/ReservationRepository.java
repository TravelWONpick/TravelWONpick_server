package wonpick.travel.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wonpick.travel.server.entity.Reservation;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
