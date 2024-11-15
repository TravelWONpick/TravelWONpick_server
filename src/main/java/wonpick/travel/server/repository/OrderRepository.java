package wonpick.travel.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wonpick.travel.server.entity.Order;
import wonpick.travel.server.entity.Passenger;
import wonpick.travel.server.entity.Reservation;

import java.util.List;
import java.util.Optional;


public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    Optional<Order> findByOrderId(String orderID);

    // 주문 ID로 예매 조회
    @Query("SELECT r FROM Reservation r WHERE r.order.id = :orderId")
    Optional<Reservation> findReservationByOrderId(@Param("orderId") Long orderId);

    // 예매 ID로 탑승객 조회
    @Query("SELECT p FROM Passenger p WHERE p.reservation.id = :reservationId")
    List<Passenger> findPassengersByReservationId(@Param("reservationId") Long reservationId);

    // 예약 ID로 항공편 리스트 조회
    @Query("SELECT rf FROM ReservationFlight rf WHERE rf.reservation.id = :reservationId")
    List<wonpick.travel.server.entity.ReservationFlight> findFlightsByReservationId(@Param("reservationId") Long reservationId);
}
