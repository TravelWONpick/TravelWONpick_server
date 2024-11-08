package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class SpecialPriceDTO {
    private Long id;
    private String destination;
    private String departureDate;
    private String title;
    private String description;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private String category;
    private Integer minPrice;
//    private List<FlightDTO> flights;
}