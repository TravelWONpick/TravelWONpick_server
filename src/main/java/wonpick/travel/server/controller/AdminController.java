package wonpick.travel.server.controller;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wonpick.travel.server.dto.BaseResponse;
import wonpick.travel.server.dto.UserDTO;
import wonpick.travel.server.service.AdminService;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("api/manager")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger logger = LogManager.getLogger(UserController.class);
    private final AdminService adminService;

    // 전체 사용자 조회 API
    @GetMapping("/member")
    public ResponseEntity<BaseResponse<List<UserDTO>>> getAllUsers() {
        logger.info("[travelwonpick] 전체 사용자 조회 요청 수신, 요청 시간 - " +
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ZonedDateTime.now()));
        try {
            List<UserDTO> users = adminService.getAllUsers();
            logger.info("[travelwonpick] 전체 사용자 조회 성공, 요청 시간 - " +
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ZonedDateTime.now()));
            return ResponseEntity.ok(BaseResponse.success(users));
        } catch (Exception e) {
            logger.warn("[travelwonpick] 전체 사용자 조회 실패, 오류 - " + e.getMessage());
            return ResponseEntity.status(500).body(
                    BaseResponse.failure("전체 사용자 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // 사용자 삭제 API
    @DeleteMapping("/member")
    public ResponseEntity<BaseResponse<String>> deleteUser(@RequestParam String email) {
        logger.info("[travelwonpick] 사용자 삭제 요청 수신, 요청 시간 - " +
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ZonedDateTime.now()));
        try {
            adminService.deleteUser(email);
            return ResponseEntity.ok(BaseResponse.success(email));
        } catch (Exception e) {
            logger.warn("[travelwonpick] 사용자 삭제 실패, 오류 - " + e.getMessage());
            return ResponseEntity.status(500).body(
                    BaseResponse.failure("사용자 삭제 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
