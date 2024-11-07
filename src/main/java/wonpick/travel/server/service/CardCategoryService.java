package wonpick.travel.server.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wonpick.travel.server.entity.Card;
import wonpick.travel.server.entity.enums.CardCategoryType;
import wonpick.travel.server.repository.CardCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardCategoryService {

    private final CardCategoryRepository cardCategoryRepository;

    public List<Card> getCardsByCategoryTypes(List<CardCategoryType> categoryTypes) {
        if (categoryTypes == null || categoryTypes.isEmpty() || categoryTypes.size() > 3) {
            throw new IllegalArgumentException("카테고리는 최소 1개에서 최대 3개까지 선택할 수 있습니다.");
        }
        return cardCategoryRepository.findCardsByCategoryTypes(categoryTypes);
    }
}
