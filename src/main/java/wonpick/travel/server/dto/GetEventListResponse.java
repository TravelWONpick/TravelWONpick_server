package wonpick.travel.server.dto;

import lombok.Data;
import java.util.List;

@Data
public class GetEventListResponse {
    private List<EventDTO> events;
}