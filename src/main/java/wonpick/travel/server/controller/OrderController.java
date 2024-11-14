package wonpick.travel.server.controller;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wonpick.travel.server.dto.BaseResponse;
import wonpick.travel.server.dto.PostOrderRequest;
import wonpick.travel.server.dto.PostOrderResponse;
import wonpick.travel.server.service.OrderService;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;
    private static final Logger logger = LogManager.getLogger(OrderController.class);

    // 클라이언트로부터 주문 정보를 받아 임시 저장
    @PostMapping("/order/create")
    public ResponseEntity<?> createOrder(@RequestHeader("Authorization") String authHeader,
                                         @RequestBody PostOrderRequest postOrderRequest) {
        logger.info("OrderController.createOrder");
        try {
            String accessToken = authHeader.replace("Bearer ", "");

            PostOrderResponse orderResponse = orderService.saveOrder(accessToken, postOrderRequest.getOrderId(), postOrderRequest.getAmount());
            logger.info("order 저장 성공: " + orderResponse.getOrderSeqId());
            return new ResponseEntity<>(BaseResponse.success(orderResponse), HttpStatus.CREATED); // 저장 성공 시 201 반환

        } catch (Exception e) {

            logger.info("order 저장 실패: " + e.getMessage());

            return new ResponseEntity<>(
                    BaseResponse.failure(
                            "Order 저장에 실패하였습니다. " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
