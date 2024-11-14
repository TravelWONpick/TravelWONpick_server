package wonpick.travel.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import wonpick.travel.server.dto.PostPaymentConfirmRequest;
import wonpick.travel.server.dto.PostPaymentConfirmResponse;
import wonpick.travel.server.repository.OrderRepository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final Logger logger = LogManager.getLogger(PaymentService.class);
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final ReservationService reservationService;

    // 설정 파일에서 API URL과 Secret Key 주입받기
    @Value("${toss.api.url}")
    private String apiUrl;

    @Value("${toss.api.widget-secret-key}")
    private String widgetSecretKey;

    // 결제 요청 유효성 검사
    public boolean validatePaymentInfo(String orderId, Integer amount) {
        logger.info("PaymentService.validatePaymentInfo");

        return orderRepository.findByOrderId(orderId)
                .map(order -> {
                    boolean isValid = order.getAmount().equals(amount);

                    if (isValid) {
                        logger.info("Order validation successful");
                    } else {
                        logger.warn("Order validation failed: Amount mismatch");
                    }

                    return isValid;
                })
                .orElseGet(() -> {
                    logger.warn("Order not found for ID: " + orderId);
                    return false;
                });
    }

    @Transactional
    public PostPaymentConfirmResponse confirmPayment(PostPaymentConfirmRequest request) {
        logger.info("PaymentService.confirmPayment");
        HttpHeaders headers = createAuthHeaders();

        HttpEntity<PostPaymentConfirmRequest> entity = new HttpEntity<>(request, headers);
        try {
            // POST 요청 보내기
            ResponseEntity<PostPaymentConfirmResponse> response =
                    restTemplate.postForEntity(apiUrl, entity, PostPaymentConfirmResponse.class);

            PostPaymentConfirmResponse body = response.getBody();
            if (body == null) {
                logger.error("Payment confirmation response body is null");
                throw new RuntimeException("결제 승인 응답이 비어 있습니다.");
            }

            // Reservation 추가 로직
            Long mockDepFlightId = 1L;
            Long mockArrFlightId = 2L;
            Long mockSeatCount = 3L;
            reservationService.createReservation(
                    request,
                    body,
                    mockDepFlightId,
                    mockArrFlightId,
                    mockSeatCount);

            return body; // 성공 시 응답 반환

        } catch (HttpClientErrorException e) {
            // HTTP 4xx 클라이언트 오류 처리
            logger.error("Client error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("결제 요청 오류 (클라이언트 오류): " + e.getResponseBodyAsString());

        } catch (HttpServerErrorException e) {
            // HTTP 5xx 서버 오류 처리
            logger.error("Server error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("결제 요청 오류 (서버 오류): " + e.getResponseBodyAsString());

        } catch (Exception e) {
            // 기타 예외 처리
            logger.error("Unexpected error: " + e.getMessage());
            throw new RuntimeException("결제 요청 중 예기치 못한 오류 발생: " + e.getMessage());
        }
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String authorizationHeader = "Basic " + Base64.getEncoder()
                .encodeToString((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authorizationHeader);
        return headers;
    }
}



