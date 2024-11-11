package wonpick.travel.server.dto;

import lombok.Data;

@Data
public class PostLoginUserRequest {

    private String email;
    private String password;
}
