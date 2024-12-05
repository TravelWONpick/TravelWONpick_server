package wonpick.travel.server.controller;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wonpick.travel.server.dto.BaseResponse;
import wonpick.travel.server.dto.PostLoginUserRequest;
import wonpick.travel.server.dto.PostLoginUserResponse;
import wonpick.travel.server.dto.PostSignupUserRequest;
import wonpick.travel.server.dto.PostSignupUserResponse;
import wonpick.travel.server.dto.PostVerifySuccessUserResponse;
import wonpick.travel.server.dto.PostVerifyUserRequest;
import wonpick.travel.server.dto.PostVerifyUserResponse;
import wonpick.travel.server.dto.PostVerifyUserUserRequest;
import wonpick.travel.server.service.UserService;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);
    private final UserService userService;

    // 인증번호 전송 API
    @PostMapping("/verifyuser")
    public ResponseEntity<BaseResponse<PostVerifyUserResponse>> verifyUser(@RequestBody PostVerifyUserRequest request) {
        logger.info("[travelwonpick] 인증번호 전송 요청 수신: 회원 이메일 - " + request.getEmail() + ", 요청 시간 - " +
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ZonedDateTime.now()));
        try {
            PostVerifyUserResponse response = userService.verifyUser(request);
            logger.info("[travelwonpick] 인증번호 전송 성공: 회원 이메일 - " + request.getEmail() + ", 요청 시간 - " +
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ZonedDateTime.now()));
            return ResponseEntity.ok(BaseResponse.success(response));
        } catch (RuntimeException e) {
            logger.warn("[travelwonpick] 인증번호 전송 실패: 회원 이메일 - " + request.getEmail() + ", 오류 - " + e.getMessage());

            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("[travelwonpick] 인증번호 전송 중 알 수 없는 오류 발생: 회원 이메일 - " + request.getEmail() + ", 오류 - " + e.getMessage());

            // 사용자가 볼 수 있는 일반 메시지 전달
            String userFriendlyMessage = "인증번호 발송 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.";
            return ResponseEntity.badRequest().body(BaseResponse.failure(userFriendlyMessage, HttpStatus.BAD_REQUEST));
        }
    }

    // 인증번호 확인 API
    @PostMapping("/verifysuccess")
    public ResponseEntity<BaseResponse<PostVerifySuccessUserResponse>> verifySuccess(@RequestBody PostVerifyUserUserRequest request) {
        logger.info("[travelwonpick] 인증번호 확인 요청 수신: 회원 이메일 - " + request.getEmail() + ", 요청 시간 - " + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ZonedDateTime.now()));
        try {
            PostVerifySuccessUserResponse response = userService.verifySuccess(request);
            logger.info("[travelwonpick] 인증번호 확인 성공: 회원 이메일 - " + request.getEmail() + ", 요청 시간 - " + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ZonedDateTime.now()));
            return ResponseEntity.ok(BaseResponse.success(response));
        } catch (Exception e) {
            logger.warn("[travelwonpick] 인증번호 확인 실패: 회원 이메일 - " + request.getEmail() + ", 오류 - " + e.getMessage());
            return ResponseEntity.status(401).body(BaseResponse.failure("인증번호 확인 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST));
        }
    }

    // 최종 회원가입 - DB 저장 API
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<PostSignupUserResponse>> signUpToDatabase(@RequestBody PostSignupUserRequest request) {
        logger.info("[travelwonpick] 회원가입 요청 수신: 회원 이메일 - " + request.getEmail() + ", signuptime=" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ZonedDateTime.now()));
        try {
            userService.signUp(request);
            logger.info("[travelwonpick] 회원가입 성공: 회원 이메일 - " + request.getEmail() + ", signuptime=" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ZonedDateTime.now()));

            PostSignupUserResponse response = new PostSignupUserResponse();
            response.setMessage("회원가입이 완료되었습니다.");
            return ResponseEntity.ok(BaseResponse.success(response));

        } catch (Exception e) {
            logger.warn("[travelwonpick] 회원가입 실패: 회원 이메일 - " + request.getEmail() + ", 오류 - " + e.getMessage());
            return ResponseEntity.status(500).body(BaseResponse.failure("회원가입 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<PostLoginUserResponse>> login(@RequestBody PostLoginUserRequest request) {
        logger.info("[travelwonpick] 로그인 요청 수신: 회원 이메일 - " + request.getEmail() + ", 요청 시간 - " + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ZonedDateTime.now()));
        try {
            PostLoginUserResponse response = userService.login(request);

            logger.info("[travelwonpick] 로그인 성공: 회원 이메일 - " + request.getEmail() + ", 요청 시간 - " + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ZonedDateTime.now()));
            return ResponseEntity.ok(BaseResponse.success(response));
        } catch (Exception e) {
            logger.warn("[travelwonpick] 로그인 실패: 회원 이메일 - " + request.getEmail() + ", 오류 - " + e.getMessage());
            return ResponseEntity.status(401).body(BaseResponse.failure("로그인 실패: 이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST));
        }
    }

    // 로그아웃 API
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<String>> logout(@RequestBody Map<String, String> request) {
        logger.info("[travelwonpick] 로그아웃 요청 수신, 요청 시간 - " + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ZonedDateTime.now()));

        try {
            // accessToken 추출
            String accessToken = request.get("accessToken");
            logger.info("Received accessToken: " + accessToken);

            String response = userService.logout(accessToken);
            logger.info("[travelwonpick] 로그아웃 성공, 요청 시간 - " + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ZonedDateTime.now()));

            return ResponseEntity.ok(BaseResponse.success(response));
        } catch (Exception e) {
            logger.warn("[travelwonpick] 로그아웃 실패, 오류 - " + e.getMessage());
            return ResponseEntity.status(500).body(BaseResponse.failure("로그아웃 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
