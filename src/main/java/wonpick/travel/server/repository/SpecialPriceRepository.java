package wonpick.travel.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wonpick.travel.server.entity.SpecialPricePick;

import java.time.LocalDateTime;
import java.util.List;

public interface SpecialPriceRepository extends JpaRepository<SpecialPricePick, Long> {
    @Query("SELECT sp FROM SpecialPricePick sp LEFT JOIN FETCH sp.flights")
    List<SpecialPricePick> findAllWithFlights();

    @Query("SELECT sp FROM SpecialPricePick sp LEFT JOIN FETCH sp.flights " +
            "WHERE sp.openTime <= :now AND sp.closeTime >= :now")
    List<SpecialPricePick> findCurrentSpecialPrices(@Param("now") LocalDateTime now);

    @Query("SELECT sp FROM SpecialPricePick sp LEFT JOIN FETCH sp.flights " +
            "WHERE sp.category = :category")
    List<SpecialPricePick> findByCategory(@Param("category") String category);
}