package wonpick.travel.server.controller;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wonpick.travel.server.dto.*;
import wonpick.travel.server.dto.BaseResponse;
import wonpick.travel.server.service.MyPageService;

import java.util.List;

@RestController
@RequestMapping("/api/my")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private static final Logger logger = LogManager.getLogger(MyPageController.class);

    @GetMapping("/info")
    public ResponseEntity<BaseResponse<UserDTO>> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        logger.info("[travelwonpick] 사용자 정보 조회");
        try {
            String accessToken = authHeader.replace("Bearer ", "");
            UserDTO userResponse = myPageService.getUserInfo(accessToken);
            return ResponseEntity.ok(BaseResponse.success(userResponse));
        } catch (RuntimeException e) {
            logger.error("유효하지 않은 토큰이거나 사용자를 찾을 수 없습니다.", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.failure("유효하지 않은 토큰이거나 사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            logger.error("회원 정보 조회 중 오류가 발생했습니다.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.failure("회원 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PatchMapping("/info")
    public ResponseEntity<BaseResponse<UserDTO>> updateUserInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserDTO request) {
        logger.info("[travelwonpick] 사용자 정보 수정 요청");
        try {
            String accessToken = authHeader.replace("Bearer ", "");
            UserDTO updatedUser = myPageService.updateUserInfo(accessToken, request);
            return ResponseEntity.ok(BaseResponse.success(updatedUser));
        } catch (RuntimeException e) {
            logger.error("유효하지 않은 토큰이거나 사용자를 찾을 수 없습니다.", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.failure("유효하지 않은 토큰이거나 사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            logger.error("회원 정보 수정 중 오류가 발생했습니다.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.failure("회원 정보 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    @DeleteMapping("/account")
    public ResponseEntity<BaseResponse<String>> deleteAccount(@RequestHeader("Authorization") String authHeader) {
        logger.info("[travelwonpick] 회원 탈퇴 요청");
        try {
            String accessToken = authHeader.replace("Bearer ", "");
            myPageService.deleteAccount(accessToken);
            return ResponseEntity.ok(BaseResponse.success("회원 탈퇴가 완료되었습니다."));
        } catch (RuntimeException e) {
            logger.error("유효하지 않은 토큰이거나 사용자를 찾을 수 없습니다.", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.failure("유효하지 않은 토큰이거나 사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            logger.error("회원 탈퇴 중 오류가 발생했습니다.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.failure("회원 탈퇴 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/flight")
    public ResponseEntity<BaseResponse<List<ReservationDTO>>> getUserReservations(@RequestHeader("Authorization") String authHeader) {
        logger.info("[travelwonpick] 예약 내역 조회 요청");
        try {
            String accessToken = authHeader.replace("Bearer ", "");
            List<ReservationDTO> reservations = myPageService.getUserReservations(accessToken);
            return ResponseEntity.ok(BaseResponse.success(reservations));
        } catch (RuntimeException e) {
            logger.error("유효하지 않은 토큰이거나 사용자를 찾을 수 없습니다.", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.failure("유효하지 않은 토큰이거나 사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            logger.error("예약 내역 조회 중 오류가 발생했습니다.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.failure("예약 내역 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/flight/{uuid}/passenger-detail")
    public ResponseEntity<BaseResponse<List<ReservationPassengerDTO>>> getPassengerDetails(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("uuid") String uuid) {

        logger.info("[travelwonpick] 주문 UUID로 탑승객 정보 조회 요청");

        try {
            // Authorization 헤더에서 Bearer 토큰 추출
            String accessToken = authHeader.replace("Bearer ", "");

            // 서비스 호출하여 탑승객 정보 조회
            List<ReservationPassengerDTO> passengerDetails = myPageService.getPassengerDetailsByUuid(accessToken, uuid);

            // 성공 응답 반환
            return ResponseEntity.ok(BaseResponse.success(passengerDetails));
        } catch (RuntimeException e) {
            logger.error("유효하지 않은 토큰이거나 주문 ID를 찾을 수 없습니다.", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.failure("유효하지 않은 토큰이거나 주문 ID를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            logger.error("탑승객 정보 조회 중 오류가 발생했습니다.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.failure("탑승객 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/flight/{uuid}/flight-detail")
    public ResponseEntity<BaseResponse<ReservationFlightDTO>> getFlightDetails(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("uuid") String uuid) {

        logger.info("[travelwonpick] 주문 UUID로 항공편 상세 정보 조회 요청");

        try {
            // Authorization 헤더에서 Bearer 토큰 추출
            String accessToken = authHeader.replace("Bearer ", "");

            // 서비스 호출하여 항공편 상세 정보 조회
            ReservationFlightDTO flightDetails = myPageService.getReservationDetails(accessToken, uuid);

            // 성공 응답 반환
            return ResponseEntity.ok(BaseResponse.success(flightDetails));
        } catch (RuntimeException e) {
            logger.error("유효하지 않은 토큰이거나 주문 ID를 찾을 수 없습니다.", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.failure("유효하지 않은 토큰이거나 주문 ID를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            logger.error("항공편 상세 정보 조회 중 오류가 발생했습니다.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.failure("항공편 상세 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    @GetMapping("/passenger")
    public ResponseEntity<BaseResponse<List<PassengerDTO>>> getPassengerInfo(@RequestHeader("Authorization") String authHeader) {
        logger.info("[travelwonpick] 탑승객 정보 조회");

        try {
            // Authorization 헤더에서 Bearer 토큰 추출
            String accessToken = authHeader.replace("Bearer ", "");

            // 탑승객 정보 조회
            List<PassengerDTO> passengers = myPageService.getPassengerInfo(accessToken);
            return ResponseEntity.ok(BaseResponse.success(passengers));
        } catch (RuntimeException e) {
            logger.error("유효하지 않은 토큰이거나 사용자를 찾을 수 없습니다.", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.failure("유효하지 않은 토큰이거나 사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            logger.error("탑승객 정보 조회 중 오류가 발생했습니다.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.failure("탑승객 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // 탑승객 등록
    @PostMapping("/passenger")
    public ResponseEntity<BaseResponse<PostPassengerResponseDTO>> createPassenger(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PostPassengerRequestDTO requestDTO) {
        logger.info("[travelwonpick] 탑승객 등록");
        try {
            String accessToken = authHeader.replace("Bearer ", "");
            Long passengerId = myPageService.createPassenger(accessToken, requestDTO);

            logger.info("[travelwonpick] 탑승객 등록 성공: passengerId=" + passengerId +
                    ", birth=" + requestDTO.getBirth() +
                    ", gender=" + requestDTO.getGender());

            PostPassengerResponseDTO responseDTO = PostPassengerResponseDTO.builder()
                    .id(passengerId)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponse.success(responseDTO));
        } catch (RuntimeException e) {
            logger.error("유효하지 않은 토큰이거나 사용자를 찾을 수 없습니다.", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.failure("유효하지 않은 토큰이거나 사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            logger.error("탑승객 등록 중 오류가 발생했습니다.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.failure("탑승객 등록 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // 탑승객 정보 수정
    @PatchMapping("/passenger/{up_id}")
    public ResponseEntity<BaseResponse<UpdatePassengerResponseDTO>> updatePassenger(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("up_id") Long upId,
            @RequestBody UpdatePassengerRequestDTO requestDTO) {
        logger.info("[travelwonpick] 탑승객 정보 수정");
        try {
            String accessToken = authHeader.replace("Bearer ", "");
            UpdatePassengerResponseDTO updatedPassenger = myPageService.updatePassenger(accessToken, upId, requestDTO);
            return ResponseEntity.ok(BaseResponse.success(updatedPassenger));
        } catch (RuntimeException e) {
            logger.error("유효하지 않은 토큰이거나 사용자를 찾을 수 없습니다.", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.failure("유효하지 않은 토큰이거나 사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            logger.error("탑승객 정보 수정 중 오류가 발생했습니다.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.failure("탑승객 정보 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // 탑승객 삭제
    @DeleteMapping("/passenger/{up_id}")
    public ResponseEntity<BaseResponse<Void>> deletePassenger(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("up_id") Long upId) {
        logger.info("[travelwonpick] 탑승객 삭제");
        try {
            String accessToken = authHeader.replace("Bearer ", "");
            myPageService.deletePassenger(accessToken, upId);
            return ResponseEntity.ok(BaseResponse.success(null));
        } catch (RuntimeException e) {
            logger.error("유효하지 않은 토큰이거나 사용자를 찾을 수 없습니다.", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.failure("유효하지 않은 토큰이거나 사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED));
        } catch (Exception e) {
            logger.error("탑승객 삭제 중 오류가 발생했습니다.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.failure("탑승객 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

}
