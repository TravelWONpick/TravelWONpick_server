package wonpick.travel.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetSpecialPriceListResponse {
    List<SpecialPriceDTO> specialPrices;
}