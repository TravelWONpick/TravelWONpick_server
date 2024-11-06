package wonpick.travel.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostLoginUserRequest {

    private String email;
    private String password;

    public PostLoginUserRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
