package wonpick.travel.server.controller;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wonpick.travel.server.dto.*;
import wonpick.travel.server.dto.BaseResponse;
import wonpick.travel.server.service.PaymentService;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class PaymentController {

    private final PaymentService paymentService;
    private static final Logger logger = LogManager.getLogger(PaymentController.class);

    @PostMapping("/payments/validate")
    public ResponseEntity<?> validatePayment(@RequestBody PostPaymentValidateRequest request) {
        logger.info("PaymentController.validatePayment");

        boolean isValid = paymentService.validatePaymentInfo(
                request.getOrderId(),
                request.getAmount()
        );

        PostPaymentValidateResponse response = new PostPaymentValidateResponse();
        response.setValid(isValid);

        if (isValid) {
            response.setMessage("결제 정보 인증 성공");
            logger.info(response.toString());
            return ResponseEntity.ok().body(BaseResponse.success(response));

        } else {
            logger.info(response.toString());
            return ResponseEntity.badRequest()
                    .body(BaseResponse.failure("결제 정보가 유효하지 않습니다", HttpStatus.BAD_REQUEST));
        }
    }


    @PostMapping("/payments/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody PostPaymentConfirmRequest request) {
        logger.info("PaymentController.confirmPayment");

        logger.info("[travelwonpick] 결제 확인 요청 수신: " +
                "orderId=" + request.getOrderId() +
                ", 요청 시간=" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ZonedDateTime.now()));
        try {
            PostPaymentConfirmResponse response = paymentService.confirmPayment(request);

            logger.info("[travelwonpick] 결제 확인 성공: orderId=" + request.getOrderId() +
                    ", 탑승객 수=" + request.getSeatCount());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("[travelwonpick] 결제 실패: orderId=" + request.getOrderId(), e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
