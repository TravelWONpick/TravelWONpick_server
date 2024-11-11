package wonpick.travel.server.dto;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostSignupUserRequest {

    private String email;
    private String password;
    private String name;
    private String phonenumber;
    private Boolean notification;
}
