package wonpick.travel.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wonpick.travel.server.dto.CardDTO;
import wonpick.travel.server.dto.GetCardListResponse;
import wonpick.travel.server.service.CardService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private static final Logger logger = LogManager.getLogger(CardController.class);

    @GetMapping("/cards")
    public ResponseEntity<GetCardListResponse> getCard(){

        logger.info("[travelwonpick] card 조회 api 요청");
        List<CardDTO> cards = cardService.getAllCards();

        return ResponseEntity.ok(new GetCardListResponse(cards));
    }
}
