package wonpick.travel.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wonpick.travel.server.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}

