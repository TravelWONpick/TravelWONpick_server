package wonpick.travel.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wonpick.travel.server.common.exception.BaseException;
import wonpick.travel.server.common.exception.ErrorCode;
import wonpick.travel.server.dto.FlightPassengerDTO;
import wonpick.travel.server.dto.PostPaymentConfirmRequest;
import wonpick.travel.server.entity.Passenger;
import wonpick.travel.server.entity.Reservation;
import wonpick.travel.server.repository.PassengerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerRepository passengerRepository;

    public void createFlightPassenger(Reservation reservation, PostPaymentConfirmRequest request) {
        validateRequest(request);

        List<Passenger> passengers = request.getPassengers().stream()
                .map(flightPassengerDTO -> convertToPassenger(flightPassengerDTO, reservation))
                .toList();

        passengerRepository.saveAll(passengers);
    }

    private void validateRequest(PostPaymentConfirmRequest request) {
        if (request == null || request.getPassengers() == null || request.getPassengers().isEmpty()) {
            throw new BaseException(ErrorCode.INVALID_PASSENGER);
        }
    }

    private Passenger convertToPassenger(FlightPassengerDTO flightPassengerDTO, Reservation reservation) {
        return Passenger.builder()
                .reservation(reservation)
                .firstName(flightPassengerDTO.getFirstName())
                .lastName(flightPassengerDTO.getLastName())
                .gender(flightPassengerDTO.getGender())
                .birth(flightPassengerDTO.getBirthDate())
                .phoneNumber(flightPassengerDTO.getPhone())
                .build();
    }
}