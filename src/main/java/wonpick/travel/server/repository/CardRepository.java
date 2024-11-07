package wonpick.travel.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wonpick.travel.server.entity.Card;
import wonpick.travel.server.entity.User;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Query("SELECT c FROM Card c LEFT JOIN FETCH c.cardBenefits")
    List<Card> findAllWithBenefits();
}

