package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PassengerDTO {
    private Long id;
    private String lastName;
    private String firstName;
    private String birth;
    private String gender;
    private String phoneNumber;
}
