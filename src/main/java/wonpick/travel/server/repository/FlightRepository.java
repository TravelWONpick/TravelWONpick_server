package wonpick.travel.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wonpick.travel.server.dto.FlightDTO;
import wonpick.travel.server.entity.Flight;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Query("SELECT f FROM Flight f WHERE f.specialPricePick.id = :spId")
    List<Flight> findBySpecialPriceId(@Param("spId") Long spId);

    @Query("SELECT f FROM Flight f " +
            "WHERE f.departurePlace = :departurePlace " +
            "AND f.arrivalPlace = :arrivalPlace")
    List<Flight> findByRoute(
            @Param("departurePlace") String departurePlace,
            @Param("arrivalPlace") String arrivalPlace
    );

    @Query("SELECT f FROM Flight f " +
            "WHERE f.departureTime >= :startDate " +
            "AND f.departureTime <= :endDate")
    List<Flight> findByDepartureTimeBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT f FROM Flight f " +
            "WHERE f.specialPrice BETWEEN :minPrice AND :maxPrice")
    List<Flight> findByPriceRange(
            @Param("minPrice") int minPrice,
            @Param("maxPrice") int maxPrice
    );


    @Query("SELECT f FROM Flight f " +
            "WHERE f.specialPricePick.id = :spId " +
            "AND f.departureAirportCode = :departureAirportCode " +
            "AND f.arrivalAirportCode = :arrivalAirportCode " +
            "AND f.departureTime BETWEEN :startOfDay AND :endOfDay")
    List<Flight> findFlightsByDateAndLocation(@Param("spId") Long spId,
                                              @Param("departureAirportCode") String departureAirportCode,
                                              @Param("arrivalAirportCode") String arrivalAirportCode,
                                              @Param("startOfDay") LocalDateTime startOfDay,
                                              @Param("endOfDay") LocalDateTime endOfDay);
}