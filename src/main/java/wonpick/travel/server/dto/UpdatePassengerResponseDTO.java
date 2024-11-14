package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wonpick.travel.server.entity.enums.Gender;
import wonpick.travel.server.entity.UserPassenger;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePassengerResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String birth;
    private Gender gender;
    private String phoneNumber;

    public static UpdatePassengerResponseDTO from(UserPassenger passenger) {
        return UpdatePassengerResponseDTO.builder()
                .id(passenger.getId())
                .firstName(passenger.getFirstName())
                .lastName(passenger.getLastName())
                .birth(passenger.getBirth())
                .gender(passenger.getGender())
                .phoneNumber(passenger.getPhoneNumber())
                .build();
    }
}