package wonpick.travel.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostSignUpUserRequest {

    private String email;
    private String password;
    private String phoneNumber;
    private Boolean notification;

    public PostSignUpUserRequest(String email, String password, String phoneNumber, Boolean notification) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.notification = notification;
    }
}
