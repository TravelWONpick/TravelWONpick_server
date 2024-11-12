package wonpick.travel.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wonpick.travel.server.dto.BaseResponse;
import wonpick.travel.server.dto.PostOrderRequest;
import wonpick.travel.server.dto.PostOrderResponse;
import wonpick.travel.server.service.OrderService;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 클라이언트로부터 주문 정보를 받아 임시 저장
    @PostMapping("/order/create")
    public ResponseEntity<?> createOrder(@RequestBody PostOrderRequest postOrderRequest) {

        try {
            // 주문 정보 저장 시도
            PostOrderResponse order = orderService.saveOrder(postOrderRequest.getOrderId(), postOrderRequest.getAmount());
            return new ResponseEntity<>(BaseResponse.success(order), HttpStatus.CREATED); // 저장 성공 시 201 반환

        } catch (Exception e) {

            return new ResponseEntity<>(
                    BaseResponse.failure(
                            "Order 저장에 실패하였습니다. " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
