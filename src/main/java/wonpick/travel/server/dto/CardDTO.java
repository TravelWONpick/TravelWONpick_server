package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CardDTO {

    private String title;
    private String description;
    private int annualFee;
    private String image;
    private String detailLink;
    private String applyLink;
    private String type;
    private List<CategoryDTO> categories;
    private List<CardBenefitDTO> benefits;
}
