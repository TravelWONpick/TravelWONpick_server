package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostVerifyUserUserRequest {
    private String email;
    private String confirmationCode;
    private String password;
}
