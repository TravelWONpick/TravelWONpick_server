package wonpick.travel.server.entity.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    @JsonProperty("남성")
    MALE("남성"),


    @JsonProperty("여성")
    FEMALE("여성");
    private final String description;
}
