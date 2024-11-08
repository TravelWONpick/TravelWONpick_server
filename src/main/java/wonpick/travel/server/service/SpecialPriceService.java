// SpecialPriceService.java
package wonpick.travel.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wonpick.travel.server.dto.FlightDTO;
import wonpick.travel.server.dto.GetSpecialPriceListResponse;
import wonpick.travel.server.dto.SpecialPriceDTO;
import wonpick.travel.server.entity.SpecialPricePick;
import wonpick.travel.server.repository.SpecialPriceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialPriceService {

    private final SpecialPriceRepository specialPriceRepository;

    public GetSpecialPriceListResponse getAllSpecialPricesWithFlights() {
        List<SpecialPricePick> specialPrices = specialPriceRepository.findAllWithFlights();

        List<SpecialPriceDTO> specialPriceDTOs = specialPrices.stream()
                .map(specialPrice -> {
                    List<FlightDTO> flightDTOs = specialPrice.getFlights().stream()
                            .map(flight -> new FlightDTO(
                                    flight.getId(),
                                    flight.getSpecialPricePick().getId(),
                                    flight.getAirline(),
                                    flight.getFlightNumber(),
                                    flight.getDeparturePlace(),
                                    flight.getArrivalPlace(),
                                    flight.getDepartureTime(),
                                    flight.getArrivalTime(),
                                    flight.getMaxSeat(),
                                    flight.getOriginPrice(),
                                    flight.getSpecialPrice(),
                                    flight.getDepartureAirportCode(),
                                    flight.getArrivalAirportCode(),
                                    flight.getBaggage()
                            ))
                            .collect(Collectors.toList());

                    return new SpecialPriceDTO(
                            specialPrice.getId(),
                            specialPrice.getDestination(),
                            specialPrice.getDepartureDate(),
                            specialPrice.getTitle(),
                            specialPrice.getDescription(),
                            specialPrice.getOpenTime(),
                            specialPrice.getCloseTime(),
                            specialPrice.getCategory(),
                            flightDTOs
                    );
                })
                .collect(Collectors.toList());

        return new GetSpecialPriceListResponse(specialPriceDTOs);
    }
}