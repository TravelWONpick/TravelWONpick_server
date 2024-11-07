package wonpick.travel.server.dto;

import lombok.Data;

@Data
public class PostEventResponse {
    private Long id;

    public PostEventResponse(Long id) {
        this.id = id;
    }
}