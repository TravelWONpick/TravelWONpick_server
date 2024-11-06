package wonpick.travel.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostUserLoginResponse {

    private Long userId;
    private String email;
    private String message;

    public PostUserLoginResponse(Long userId, String email, String message) {
        this.userId = userId;
        this.email = email;
        this.message = message;
    }
}
