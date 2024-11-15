package wonpick.travel.server.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wonpick.travel.server.dto.*;
import wonpick.travel.server.entity.*;
import wonpick.travel.server.repository.OrderRepository;
import wonpick.travel.server.repository.ReservationRepository;
import wonpick.travel.server.repository.UserPassengerRepository;
import wonpick.travel.server.repository.UserRepository;

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

    @Transactional
    public UserDTO getUserInfo(String accessToken) {
        DecodedJWT jwt = JWT.decode(accessToken);
        String sub = jwt.getSubject();

        User user = userRepository.findBySub(sub)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        return new UserDTO(user.getEmail(), user.getName(), user.getPhoneNumber(), user.getNotification());
    }

    @Transactional
    public UserDTO updateUserInfo(String accessToken, UserDTO request) {
        // Access token에서 사용자 ID 추출
        DecodedJWT jwt = JWT.decode(accessToken);
        String sub = jwt.getSubject();

        // 사용자 검색
        User user = userRepository.findBySub(sub)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        // 요청 값에 따라 사용자 정보 업데이트
        if (isNotEmpty(request.getEmail())) user.setEmail(request.getEmail());
        if (isNotEmpty(request.getName())) user.setName(request.getName());
        if (isNotEmpty(request.getPhoneNumber())) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getNotification() != null) user.setNotification(request.getNotification());

        // 변경된 사용자 정보 저장
        userRepository.save(user);

        // 변경된 사용자 정보를 기반으로 UserDTO 반환
        return new UserDTO(
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getNotification()
        );
    }

    // 유효성 검사 메서드 (null 및 빈 문자열 체크)
    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    @Transactional
    public void deleteAccount(String accessToken) {
        DecodedJWT jwt = JWT.decode(accessToken);
        String sub = jwt.getSubject();

        User user = userRepository.findBySub(sub)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        userRepository.delete(user);
    }

    @Transactional
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

    public List<ReservationPassengerDTO> getPassengerDetailsByUuid(String accessToken, String uuid) {
        // JWT 토큰에서 사용자 식별자(sub) 추출
        DecodedJWT jwt = JWT.decode(accessToken);
        String userSub = jwt.getSubject();

        // 주문 UUID로 주문 조회
        Order order = orderRepository.findByOrderId(uuid)
                .orElseThrow(() -> new RuntimeException("해당 주문 UUID에 해당하는 주문을 찾을 수 없습니다."));

        // 사용자 검증: 주문 정보와 JWT 사용자 정보가 일치하는지 확인
        if (!order.getUser().getSub().equals(userSub)) {
            throw new RuntimeException("주문 정보와 사용자 정보가 일치하지 않습니다.");
        }

        // 주문 ID로 예매 정보 조회
        Reservation reservation = orderRepository.findReservationByOrderId(order.getId())
                .orElseThrow(() -> new RuntimeException("주문 ID에 해당하는 예매 정보를 찾을 수 없습니다."));

        // 예매 ID로 탑승객 정보 조회
        List<Passenger> passengers = orderRepository.findPassengersByReservationId(reservation.getId());

        // Entity → DTO 변환
        return passengers.stream()
                .map(passenger -> new ReservationPassengerDTO(
                        passenger.getFirstName(),
                        passenger.getLastName(),
                        passenger.getGender(),
                        passenger.getBirth(),
                        passenger.getPhoneNumber()
                ))
                .collect(Collectors.toList());
    }

    public ReservationFlightDTO getReservationDetails(String accessToken, String uuid) {
        // JWT 토큰에서 사용자 식별자 추출
        DecodedJWT jwt = JWT.decode(accessToken);
        String userSub = jwt.getSubject();

        // 주문 UUID로 주문 조회
        Order order = orderRepository.findByOrderId(uuid)
                .orElseThrow(() -> new RuntimeException("해당 주문 UUID에 해당하는 주문을 찾을 수 없습니다."));

        // 사용자 검증
        if (!order.getUser().getSub().equals(userSub)) {
            throw new RuntimeException("주문 정보와 사용자 정보가 일치하지 않습니다.");
        }

        // 주문 ID로 예약 조회
        Reservation reservation = orderRepository.findReservationByOrderId(order.getId())
                .orElseThrow(() -> new RuntimeException("주문 ID에 해당하는 예약 정보를 찾을 수 없습니다."));

        // 예약 ID로 항공편 리스트 조회
        List<ReservationFlight> flights = orderRepository.findFlightsByReservationId(reservation.getId());

        if (flights.size() != 2) {
            throw new RuntimeException("항공편 정보가 유효하지 않습니다.");
        }

        // 가는 편과 오는 편 항공편 구분
        ReservationFlight outFlight = flights.get(0);
        ReservationFlight inFlight = flights.get(1);

        // 항공편 정보 가져오기
        Flight outFlightDetails = outFlight.getFlight();
        Flight inFlightDetails = inFlight.getFlight();

        // 좌석 수 확인 (기본값: 0)
        long seatCount = reservation.getSeatCount() != null ? reservation.getSeatCount() : 0L;

        // 가는 편과 오는 편의 originPrice 합산 후 seatCount 곱하기
        long outOriginPrice = outFlightDetails.getOriginPrice(); // 기본값 0이 보장됨
        long inOriginPrice = inFlightDetails.getOriginPrice();   // 기본값 0이 보장됨
        long originPrice = (outOriginPrice + inOriginPrice) * seatCount;

        // 예약 총 금액 가져오기
        long reservationAmount = reservation.getTotalAmount();

        // 할인 금액 계산
        long discount = originPrice - reservationAmount;

        // DTO 변환
        return new ReservationFlightDTO(
                seatCount,
                originPrice,
                discount,
                (long) order.getAmount(),
                outFlightDetails.getFlightNumber(),
                outFlightDetails.getDeparturePlace() + " → " + outFlightDetails.getArrivalPlace(),
                outFlightDetails.getDepartureTime() != null ? outFlightDetails.getDepartureTime().toString() : "출발 시간 정보 없음",
                outFlightDetails.getArrivalTime() != null ? outFlightDetails.getArrivalTime().toString() : "도착 시간 정보 없음",
                inFlightDetails.getFlightNumber(),
                inFlightDetails.getDeparturePlace() + " → " + inFlightDetails.getArrivalPlace(),
                inFlightDetails.getDepartureTime() != null ? inFlightDetails.getDepartureTime().toString() : "출발 시간 정보 없음",
                inFlightDetails.getArrivalTime() != null ? inFlightDetails.getArrivalTime().toString() : "도착 시간 정보 없음"
        );
    }





    @Transactional
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
