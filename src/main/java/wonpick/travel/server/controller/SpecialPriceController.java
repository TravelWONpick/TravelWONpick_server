package wonpick.travel.server.controller;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wonpick.travel.server.dto.*;
import wonpick.travel.server.dto.BaseResponse;
import wonpick.travel.server.service.FlightService;
import wonpick.travel.server.service.SpecialPriceService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SpecialPriceController {
    private final SpecialPriceService specialPriceService;
    private final FlightService flightService;
    private static final Logger logger = LogManager.getLogger(SpecialPriceController.class);

    @GetMapping("/special")
    public ResponseEntity<BaseResponse<GetSpecialPriceListResponse>> getSpecialPrices() {
        logger.info("[teavelwonpick] 특가픽 리스트 조회");
        GetSpecialPriceListResponse allSpecialPricesWithFlights = specialPriceService.getAllSpecialPricesWithFlights();
        return ResponseEntity.ok(BaseResponse.success(allSpecialPricesWithFlights));
    }

    @GetMapping("/special/{spId}")
    public ResponseEntity<BaseResponse<RoundTripFlightResponse>> getFlight(@PathVariable Long spId,
                                                                           @RequestParam String departureDate,
                                                                           @RequestParam String arrivalDate,
                                                                           @RequestParam String depAirportCode,
                                                                           @RequestParam String arrAirportCode) {


        logger.info(departureDate);
        logger.info("[teavelwonpick] 특가픽 지역 조회 - " + arrAirportCode);
        List<FlightDTO> outboundFlights = flightService.searchFlights(spId, depAirportCode, arrAirportCode, departureDate);

        List<FlightDTO> returnFlights = flightService.searchFlights(spId, arrAirportCode, depAirportCode, arrivalDate);


        return ResponseEntity.ok(BaseResponse.success(new RoundTripFlightResponse(outboundFlights, returnFlights)));
    }

}
