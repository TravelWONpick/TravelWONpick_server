package wonpick.travel.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import wonpick.travel.server.entity.enums.Gender;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePassengerRequestDTO {
    private String firstName;
    private String lastName;
    private String birth;
    private Gender gender;
    private String phoneNumber;
}