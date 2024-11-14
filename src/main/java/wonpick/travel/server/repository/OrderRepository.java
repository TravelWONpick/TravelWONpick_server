package wonpick.travel.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wonpick.travel.server.entity.Order;

import java.util.List;
import java.util.Optional;


public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);


    Optional<Order> findByOrderId(String orderID);
}
