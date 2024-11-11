package wonpick.travel.server.dto;

import lombok.Data;

@Data
public class PostVerifyUserRequest {
    private String email;
    private String password;
    private String name;
    private String phonenumber;
}
