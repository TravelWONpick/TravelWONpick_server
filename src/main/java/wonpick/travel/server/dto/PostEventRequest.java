package wonpick.travel.server.dto;

import lombok.Data;
import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostEventRequest {
    private String title;
    private MultipartFile image; // MultipartFile로 변경
    private MultipartFile previewImage; // MultipartFile로 변경
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
