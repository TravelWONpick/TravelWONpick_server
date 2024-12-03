package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class
EventDetailDTO {
    private Long id;
    private String title;
    private String image;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}