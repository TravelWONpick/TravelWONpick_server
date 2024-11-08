package wonpick.travel.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wonpick.travel.server.dto.BaseResponse;
import wonpick.travel.server.dto.GetSpecialPriceListResponse;
import wonpick.travel.server.dto.SpecialPriceDTO;
import wonpick.travel.server.service.SpecialPriceService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SpecialPriceController {
    private final SpecialPriceService specialPriceService;

    @GetMapping("/special")
    public ResponseEntity<BaseResponse<GetSpecialPriceListResponse>> getSpecialPrices() {
        GetSpecialPriceListResponse allSpecialPricesWithFlights = specialPriceService.getAllSpecialPricesWithFlights();
        return ResponseEntity.ok(BaseResponse.success(allSpecialPricesWithFlights));
    }
}
