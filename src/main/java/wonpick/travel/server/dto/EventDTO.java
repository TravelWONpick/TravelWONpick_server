package wonpick.travel.server.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventDTO {
    private String title;
    private String previewImage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}