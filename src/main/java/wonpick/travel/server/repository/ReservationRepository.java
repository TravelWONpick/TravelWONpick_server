package wonpick.travel.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wonpick.travel.server.entity.Reservation;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
//    List<Reservation> findByUserId(Long userId);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.order WHERE r.order.id = :orderSeqId")
    Reservation findByOrderWithOrderSeqId(@Param("orderSeqId") Long orderSeqId);
    }
