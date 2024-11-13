package wonpick.travel.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wonpick.travel.server.entity.UserPassenger;
import java.util.List;

public interface UserPassengerRepository extends JpaRepository<UserPassenger, Long> {
    List<UserPassenger> findByUser_Sub(String sub);
}
