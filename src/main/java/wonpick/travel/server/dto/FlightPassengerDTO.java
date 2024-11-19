package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import wonpick.travel.server.entity.enums.Gender;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class FlightPassengerDTO {
    private String birthDate;
    private String firstName;
    private Gender gender;
    private String lastName;
    private String phone;
}
