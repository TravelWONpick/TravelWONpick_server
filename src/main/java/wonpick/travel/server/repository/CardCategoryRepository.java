package wonpick.travel.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wonpick.travel.server.entity.Card;
import wonpick.travel.server.entity.CardCategory;
import wonpick.travel.server.entity.enums.CardCategoryType;

import java.util.List;

public interface CardCategoryRepository extends JpaRepository<CardCategory, Long> {

    @Query("SELECT DISTINCT cc.card FROM CardCategory cc WHERE cc.category.type IN :categoryTypes")
    List<Card> findCardsByCategoryTypes(@Param("categoryTypes") List<CardCategoryType> categoryTypes);
}
