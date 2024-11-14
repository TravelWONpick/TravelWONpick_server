package wonpick.travel.server.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wonpick.travel.server.dto.PassengerDTO;
import wonpick.travel.server.dto.ReservationDTO;
import wonpick.travel.server.dto.UserDTO;
import wonpick.travel.server.entity.Order;
import wonpick.travel.server.entity.Reservation;
import wonpick.travel.server.entity.User;
import wonpick.travel.server.entity.UserPassenger;
import wonpick.travel.server.repository.OrderRepository;
import wonpick.travel.server.repository.ReservationRepository;
import wonpick.travel.server.repository.UserPassengerRepository;
import wonpick.travel.server.repository.UserRepository;
import wonpick.travel.server.dto.PostPassengerRequestDTO;
import wonpick.travel.server.dto.UpdatePassengerRequestDTO;
import wonpick.travel.server.dto.UpdatePassengerResponseDTO;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final UserPassengerRepository userPassengerRepository;
    private final ReservationRepository reservationRepository;
    private final OrderRepository orderRepository;

    public UserDTO getUserInfo(String accessToken) {
        DecodedJWT jwt = JWT.decode(accessToken);
        String sub = jwt.getSubject();

        User user = userRepository.findBySub(sub)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        return new UserDTO(user.getEmail(), user.getName(), user.getPhoneNumber());
    }

    public UserDTO updateUserInfo(String accessToken, UserDTO request) {
        DecodedJWT jwt = JWT.decode(accessToken);
        String sub = jwt.getSubject();

        User user = userRepository.findBySub(sub)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());

        userRepository.save(user);

        return new UserDTO(user.getEmail(), user.getName(), user.getPhoneNumber());
    }

    @Transactional
    public void deleteAccount(String accessToken) {
        DecodedJWT jwt = JWT.decode(accessToken);
        String sub = jwt.getSubject();

        User user = userRepository.findBySub(sub)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        userRepository.delete(user);
    }

    public List<ReservationDTO> getUserReservations(String accessToken) {
        // JWT 토큰에서 sub 값 추출
        DecodedJWT jwt = JWT.decode(accessToken);
        String sub = jwt.getSubject();

        // sub 값으로 사용자 조회
        Long userId = userRepository.findBySub(sub)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다.")).getId();

        // 해당 사용자의 주문 내역 조회
        List<Order> orders = orderRepository.findByUserId(userId);


        // 해당 사용자의 예약 내역 조회
        ArrayList<Reservation> reservations = new ArrayList<>();
        orders.forEach(order -> {
            Reservation reservation = reservationRepository.findByOrderWithOrderSeqId(order.getId());
            reservations.add(reservation);
        });


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // 필요한 정보만 포함하는 DTO로 변환
        return reservations.stream()
                .map(reservation -> new ReservationDTO(
                        reservation.getOrder().getOrderId(),
                        reservation.getBoardingDate().format(formatter),
                        reservation.getJourney(),
                        reservation.getSeatCount()
                ))
                .collect(Collectors.toList());
    }

    public List<PassengerDTO> getPassengerInfo(String accessToken) {
        DecodedJWT jwt = JWT.decode(accessToken);
        String sub = jwt.getSubject();

        // sub 값을 이용해 UserPassenger 목록 조회
        List<UserPassenger> passengers = userPassengerRepository.findByUser_Sub(sub);

        // UserPassenger 엔티티를 PassengerDTO로 변환하여 반환
        return passengers.stream()
                .map(passenger -> new PassengerDTO(
                        passenger.getId(),
                        passenger.getLastName(),
                        passenger.getFirstName(),
                        passenger.getBirth(),
                        passenger.getGender().toString(),
                        passenger.getPhoneNumber()
                ))
                .collect(Collectors.toList());
    }
    @Transactional
    public Long createPassenger(String accessToken, PostPassengerRequestDTO requestDTO) {
        DecodedJWT jwt = JWT.decode(accessToken);
        String sub = jwt.getSubject();

        User user = userRepository.findBySub(sub)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        UserPassenger passenger = UserPassenger.builder()
                .user(user)
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .birth(requestDTO.getBirth())
                .gender(requestDTO.getGender())
                .phoneNumber(requestDTO.getPhoneNumber())
                .build();

        UserPassenger savedPassenger = userPassengerRepository.save(passenger);
        return savedPassenger.getId();
    }
    // 탑승객 정보 수정
    @Transactional
    public UpdatePassengerResponseDTO updatePassenger(String accessToken, Long upId, UpdatePassengerRequestDTO requestDTO) {
        DecodedJWT jwt = JWT.decode(accessToken);
        String sub = jwt.getSubject();

        // 현재 사용자 확인
        User user = userRepository.findBySub(sub)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        // 수정하려는 탑승객 정보 조회
        UserPassenger passenger = userPassengerRepository.findById(upId)
                .orElseThrow(() -> new RuntimeException("탑승객을 찾을 수 없습니다."));

        // 현재 사용자의 탑승객인지 확인
        if (!passenger.getUser().getSub().equals(sub)) {
            throw new RuntimeException("해당 탑승객에 대한 수정 권한이 없습니다.");
        }

        // 엔티티 업데이트
        UserPassenger updatedPassenger = UserPassenger.builder()
                .id(upId)
                .user(user)
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .birth(requestDTO.getBirth())
                .gender(requestDTO.getGender())
                .phoneNumber(requestDTO.getPhoneNumber())
                .build();

        // 저장하고 응답 DTO로 변환하여 반환
        updatedPassenger = userPassengerRepository.save(updatedPassenger);
        return UpdatePassengerResponseDTO.from(updatedPassenger);
    }
    // 탑승객 삭제
    @Transactional
    public void deletePassenger(String accessToken, Long upId) {
        DecodedJWT jwt = JWT.decode(accessToken);
        String sub = jwt.getSubject();

        // 삭제하려는 탑승객 정보 조회
        UserPassenger passenger = userPassengerRepository.findById(upId)
                .orElseThrow(() -> new RuntimeException("탑승객을 찾을 수 없습니다."));

        // 현재 사용자의 탑승객인지 확인
        if (!passenger.getUser().getSub().equals(sub)) {
            throw new RuntimeException("해당 탑승객에 대한 삭제 권한이 없습니다.");
        }

        userPassengerRepository.delete(passenger);
    }
}
