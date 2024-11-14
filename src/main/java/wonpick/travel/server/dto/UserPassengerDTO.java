package wonpick.travel.server.dto;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import wonpick.travel.server.entity.UserPassenger;
import wonpick.travel.server.entity.enums.Gender;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPassengerDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String birth;
    private Gender gender;
    private String phoneNumber;

    public static UserPassengerDTO from(UserPassenger entity) {
        return UserPassengerDTO.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .birth(entity.getBirth())
                .gender(entity.getGender())
                .phoneNumber(entity.getPhoneNumber())
                .build();
    }
}