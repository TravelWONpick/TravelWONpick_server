package wonpick.travel.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wonpick.travel.server.dto.CardDTO;
import wonpick.travel.server.dto.GetCardListResponse;
import wonpick.travel.server.service.CardService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor

public class CardController {

    private final CardService cardService;

    @GetMapping("/cards")
    public ResponseEntity<GetCardListResponse> getCard(){
        log.debug("[CardController.getCard]");
        List<CardDTO> cards = cardService.getAllCardsWithBenefits();

        return ResponseEntity.ok(new GetCardListResponse(cards));
    }
}
