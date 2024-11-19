package wonpick.travel.server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import wonpick.travel.server.common.exception.BaseException;
import wonpick.travel.server.common.exception.ErrorCode;
import wonpick.travel.server.dto.PostPaymentConfirmRequest;
import wonpick.travel.server.dto.PostPaymentConfirmResponse;
import wonpick.travel.server.repository.OrderRepository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final Logger logger = LogManager.getLogger(PaymentService.class);
    private static final int LOCK_WAIT_TIME = 10;
    private static final int LOCK_LEASE_TIME = 5;

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final ReservationService reservationService;
    private final RedissonClient redissonClient;

    @Value("${toss.api.url}")
    private String apiUrl;

    @Value("${toss.api.widget-secret-key}")
    private String widgetSecretKey;

    // 결제 요청 검증
    public boolean validatePaymentInfo(String orderId, Integer amount) {
        logger.debug("Validating payment info for orderId: {} with amount: {}", orderId, amount);

        return orderRepository.findByOrderId(orderId)
                .map(order -> {
                    if (!order.getAmount().equals(amount)) {
                        throw new BaseException(
                                ErrorCode.INVALID_PAYMENT_AMOUNT,
                                Map.of("orderId", orderId,
                                        "expectedAmount", order.getAmount(),
                                        "actualAmount", amount)
                        );
                    }
                    return true;
                })
                .orElseThrow(() ->
                        new BaseException(
                                ErrorCode.RESOURCE_NOT_FOUND,
                                "Order 정보를 불러올 수 없습니다.",
                                Map.of("orderId", orderId))
                );
    }


    // 결제 승인 요청을 토스 API에서 전달, 이후 결제 관련 로직 처리
    @Transactional
    public PostPaymentConfirmResponse confirmPayment(PostPaymentConfirmRequest request) {
        logger.debug("Processing payment confirmation for request: {}", request);

        RLock[] locks = acquireLocks(request);
        try {
            return processPaymentConfirmation(request);
        } finally {
            releaseLocks(locks);
        }
    }

    private RLock[] acquireLocks(PostPaymentConfirmRequest request) {
        // flight 에 대한 lock 얻음
        RLock depFlightLock = redissonClient.getLock(getFlightLockKey(request.getDepFlightId()));
        RLock arrFlightLock = redissonClient.getLock(getFlightLockKey(request.getArrFlightId()));

        try {
            // Lock 획득 시도
            boolean depLockAcquired = depFlightLock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
            boolean arrLockAcquired = arrFlightLock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);

            if (!(depLockAcquired && arrLockAcquired)) {
                throw new BaseException(ErrorCode.LOCK_ACQUISITION_FAILED,
                        Map.of("departure Flight Lock", depLockAcquired,
                                "arrival Flight Lock", arrLockAcquired));
            }

            return new RLock[]{depFlightLock, arrFlightLock};

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BaseException(ErrorCode.LOCK_ACQUISITION_FAILED, "Lock acquisition interrupted", e);
        }
    }

    private String getFlightLockKey(Long flightId) {
        return String.format("flight:seats:%s", flightId);
    }

    // 결제 승인 비즈니스 로직
    private PostPaymentConfirmResponse processPaymentConfirmation(PostPaymentConfirmRequest request) {
        try {

            // Request 구성
            HttpEntity<PostPaymentConfirmRequest> entity = new HttpEntity<>(request, createAuthHeaders());

            // 승인 요청
            ResponseEntity<PostPaymentConfirmResponse> response =
                    restTemplate.postForEntity(apiUrl, entity, PostPaymentConfirmResponse.class);

            PostPaymentConfirmResponse paymentResponse = response.getBody();
            if (paymentResponse == null) {
                throw new BaseException(ErrorCode.PAYMENT_FAILED, "결제 승인 응답이 올바르지 않습니다.");
            }

            // Reservation 생성
            reservationService.createReservation(
                    request,
                    paymentResponse,
                    request.getDepFlightId(),
                    request.getArrFlightId(),
                    request.getSeatCount()
            );
            return paymentResponse;

        } catch (Exception e) {
            logger.error("Payment confirmation failed", e);
            throw new BaseException(ErrorCode.PAYMENT_FAILED, e.getMessage(), e);
        }
    }

    private void releaseLocks(RLock[] locks) {
        for (RLock lock : locks) {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedAuth = Base64.getEncoder()
                .encodeToString((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);
        return headers;
    }
}