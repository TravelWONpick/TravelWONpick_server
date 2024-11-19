package wonpick.travel.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wonpick.travel.server.entity.Passenger;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}
