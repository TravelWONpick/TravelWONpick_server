package wonpick.travel.server.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class
EventDetailDTO {
    private Long id;
    private String title;
    private String image;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}