package wonpick.travel.server.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import wonpick.travel.server.controller.CardController;
import wonpick.travel.server.dto.CardBenefitDTO;
import wonpick.travel.server.dto.CardDTO;
import wonpick.travel.server.dto.CategoryDTO;
import wonpick.travel.server.entity.Card;
import wonpick.travel.server.repository.CardRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public List<CardDTO> getAllCards() {
        List<Card> cards = cardRepository.findAllWithCategoriesAndBenefits();
        return cards.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    private CardDTO convertToDTO(Card card) {
        // CardBenefit 리스트를 CardBenefitDTO 리스트로 변환
        List<CardBenefitDTO> benefits = card.getCardBenefits().stream()
                .map(benefit -> new CardBenefitDTO(
                        benefit.getName(),
                        benefit.getDetail(),
                        benefit.getImage()
                ))
                .collect(Collectors.toList());

        // Category 리스트를 CategoryDTO 리스트로 변환
        List<CategoryDTO> categories = card.getCardCategories().stream()
                .map(cardCategory -> new CategoryDTO(
                        cardCategory.getCategory().getType().getDescription() // Enum 값을 문자열로 변환
                ))
                .collect(Collectors.toList());

        // CardDTO 생성
        return new CardDTO(
                card.getName(),
                card.getDescription(),
                card.getAnnualFee(),
                card.getImage(),
                card.getDetailLink(),
                card.getApplyLink(),
                card.getType().getType(),
                categories,
                benefits
        );
    }

}
