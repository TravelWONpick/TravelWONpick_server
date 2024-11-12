package wonpick.travel.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wonpick.travel.server.entity.Reservation;
import wonpick.travel.server.entity.ReservationFlight;
import wonpick.travel.server.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // sub 값을 기준으로 사용자 조회
    Optional<User> findBySub(String sub);

    // 특정 사용자의 모든 예약 조회
//    @Query("SELECT r FROM Reservation r WHERE r.user = :user")
//    List<Reservation> findReservationsByUser(@Param("user") User user);

    // 특정 예약에 연결된 모든 ReservationFlight 조회
    @Query("SELECT rf FROM ReservationFlight rf WHERE rf.reservation = :reservation")
    List<ReservationFlight> findReservationFlightsByReservation(@Param("reservation") Reservation reservation);
}

