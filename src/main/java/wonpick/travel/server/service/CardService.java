package wonpick.travel.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wonpick.travel.server.dto.CardBenefitDTO;
import wonpick.travel.server.dto.CardDTO;
import wonpick.travel.server.entity.Card;
import wonpick.travel.server.repository.CardRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public List<CardDTO> getAllCardsWithBenefits() {
        List<Card> cards = cardRepository.findAllWithBenefits();


        return cards.stream().map(card -> {
            List<CardBenefitDTO> cardBenefitDTOs = card.getCardBenefits().stream()
                    .map(benefit -> new CardBenefitDTO(
                            benefit.getName(),
                            benefit.getDetail(),
                            benefit.getImage()
                    ))
                    .collect(Collectors.toList());

            return new CardDTO(
                    card.getImage(),
                    card.getName(),
                    card.getDescription(),
                    card.getDetailLink(),
                    card.getApplyLink(),
                    cardBenefitDTOs
            );
        }).collect(Collectors.toList());

    }
}
