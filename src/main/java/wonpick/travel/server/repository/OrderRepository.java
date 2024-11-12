package wonpick.travel.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wonpick.travel.server.entity.Order;


public interface OrderRepository extends JpaRepository<Order, Long> {
}
