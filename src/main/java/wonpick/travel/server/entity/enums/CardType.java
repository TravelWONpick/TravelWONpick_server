package wonpick.travel.server.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CardType {
    CREDIT("신용카드"),
    CHECK("체크카드");

    private final String type;

}
