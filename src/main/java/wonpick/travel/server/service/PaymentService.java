package wonpick.travel.server.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import wonpick.travel.server.controller.OrderController;
import wonpick.travel.server.dto.PostPaymentConfirmRequest;
import wonpick.travel.server.dto.PostPaymentConfirmResponse;
import wonpick.travel.server.entity.Order;
import wonpick.travel.server.repository.OrderRepository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final Logger logger = LogManager.getLogger(PaymentService.class);
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    private static final String API_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    public boolean validatePaymentInfo(String orderId, Integer amount) {
        logger.info("PaymentService.validatePaymentInfo");

        try {
            Order order = orderRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("주문 정보를 찾을 수 없습니다"));

            if (order.getAmount().equals(amount)) {
                logger.info("order 인증 성공");
            }

            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public PostPaymentConfirmResponse confirmPayment(PostPaymentConfirmRequest request) {
        logger.info("PaymentService.confirmPayment");
        HttpHeaders headers = new HttpHeaders();

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((WIDGET_SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);


        headers.set("Content-Type", "application/json");
        headers.add("Authorization",authorizations);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PostPaymentConfirmRequest> entity = new HttpEntity<>(request, headers);

        // POST 요청 보내기
        ResponseEntity<PostPaymentConfirmResponse> response =
                restTemplate.postForEntity(API_URL, entity, PostPaymentConfirmResponse.class);

        logger.info(response.toString());

        // Reservation 추가


        // 응답 반환
        PostPaymentConfirmResponse body = response.getBody();
        return response.getBody();
    }



}
