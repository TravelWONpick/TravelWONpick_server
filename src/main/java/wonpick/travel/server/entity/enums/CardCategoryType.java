package wonpick.travel.server.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CardCategoryType {

    ALL_MERCHANTS("모든 가맹점"),
    PUBLIC_TRANSPORT("대중교통"),
    FUEL_CAR("주유/자동차"),
    TELECOMMUNICATION("통신"),
    SHOPPING_MART("쇼핑/마트"),
    DINING("외식"),
    COFFEE("커피"),
    ONLINE("온라인"),
    OTT_DELIVERY("OTT/배달"),
    MAINTENANCE_FEE("관리비"),
    TRAVEL("여행/해외"),
    LODGING_VALET_PARKING("숙박/발렛파킹"),
    AIRPORT_LOUNGE("공항 라운지"),
    PREMIUM("프리미엄"),
    PETS("애완동물"),
    HOSPITAL("병원"),
    EDUCATION_PARENTING("교육/육아"),
    MOVIES_CULTURE("영화/문화"),
    LEISURE_SPORTS("레저/스포츠");

    private final String description;
}
