package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class UserDTO {
    private String email;
    private String name;
    private String phoneNumber;
    private Boolean notification;
}
