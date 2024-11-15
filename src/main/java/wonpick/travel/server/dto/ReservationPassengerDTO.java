package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wonpick.travel.server.entity.enums.Gender;

@Getter
@AllArgsConstructor
public class ReservationPassengerDTO {
    private String firstName;
    private String lastName;
    private Gender gender;
    private String birth;
    private String phoneNumber;
}
