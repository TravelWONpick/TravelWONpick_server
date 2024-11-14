package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostLoginUserResponse {

    private String message;
    private String accessToken;
    private String name;
    private Boolean isAdmin;

    //test
    //private String idToken;
}
