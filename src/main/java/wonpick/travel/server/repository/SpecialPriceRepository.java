package wonpick.travel.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wonpick.travel.server.dto.SpecialPriceDTO;
import wonpick.travel.server.entity.SpecialPricePick;

import java.time.LocalDateTime;
import java.util.List;

public interface SpecialPriceRepository extends JpaRepository<SpecialPricePick, Long> {
    @Query("SELECT sp FROM SpecialPricePick sp LEFT JOIN FETCH sp.flights")
    List<SpecialPricePick> findAllWithFlights();

    @Query("SELECT sp FROM SpecialPricePick sp LEFT JOIN FETCH sp.flights " +
            "WHERE sp.closeTime >= :now")
    List<SpecialPricePick> findCurrentSpecialPrices(@Param("now") LocalDateTime now);

    @Query("SELECT sp FROM SpecialPricePick sp LEFT JOIN FETCH sp.flights " +
            "WHERE sp.category = :category")
    List<SpecialPricePick> findByCategory(@Param("category") String category);

    @Query("SELECT new wonpick.travel.server.dto.SpecialPriceDTO(" +
            "sp.id, sp.destination, sp.departureDate, sp.title, sp.description, " +
            "sp.openTime, sp.closeTime, sp.category, MIN(f.specialPrice), sp.imageUrl," +
            "sp.arrAirportCode, sp.depAirportCode) " +
            "FROM SpecialPricePick sp " +
            "JOIN sp.flights f " +
            "GROUP BY sp.id, sp.destination, sp.departureDate, sp.title, sp.description, " +
            "sp.openTime, sp.closeTime, sp.category")
    List<SpecialPriceDTO> findAllWithMinPrice();

}