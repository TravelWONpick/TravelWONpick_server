package wonpick.travel.server.Controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wonpick.travel.server.controller.PaymentController;
import wonpick.travel.server.dto.*;
import wonpick.travel.server.entity.enums.Gender;
import wonpick.travel.server.service.PaymentService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(PaymentController.class) // PaymentController를 테스트 대상으로 지정
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc 객체를 통해 HTTP 요청을 시뮬레이션하고 응답을 확인

    @MockBean
    private PaymentService paymentService; // PaymentService를 Mock으로 대체하여 독립적으로 테스트 가능

    // 1. 결제 인증 성공 테스트
    @Test
    void shouldValidatePaymentSuccessfully() throws Exception {
        // Arrange: PaymentService의 validatePaymentInfo 메서드가 true를 반환하도록 Mock 설정
        when(paymentService.validatePaymentInfo("order123", 10000)).thenReturn(true);

        // Act & Assert: MockMvc로 POST 요청을 보내고, 예상되는 응답을 검증
        mockMvc.perform(post("/payments/validate")
                        .contentType(MediaType.APPLICATION_JSON) // 요청의 Content-Type을 JSON으로 설정
                        .content("{\"orderId\":\"order123\", \"amount\":10000}")) // 요청 본문
                .andExpect(status().isOk()) // HTTP 200 OK 응답 확인
                .andExpect(jsonPath("$.data.valid").value(true)) // 결제 인증 성공 여부 확인
                .andExpect(jsonPath("$.data.message").value("결제 정보 인증 성공")) // 성공 메시지 확인
                .andDo(print()); // 요청 및 응답 출력
    }

    // 2. 결제 인증 실패 테스트
    @Test
    void shouldReturnBadRequestForInvalidPayment() throws Exception {
        // Arrange: PaymentService의 validatePaymentInfo 메서드가 false를 반환하도록 Mock 설정
        when(paymentService.validatePaymentInfo("order123", 10000)).thenReturn(false);

        // Act & Assert: MockMvc로 POST 요청을 보내고, 예상되는 응답을 검증
        mockMvc.perform(post("/payments/validate")
                        .contentType(MediaType.APPLICATION_JSON) // 요청의 Content-Type을 JSON으로 설정
                        .content("{\"orderId\":\"order123\", \"amount\":10000}")) // 요청 본문
                .andExpect(status().isBadRequest()) // HTTP 400 Bad Request 응답 확인
                .andExpect(jsonPath("$.message").value("결제 정보가 유효하지 않습니다")) // 실패 메시지 확인
                .andDo(print()); // 요청 및 응답 출력
    }

    // 3. 결제 확인 성공 테스트
    @Test
    void shouldConfirmPaymentSuccessfully() throws Exception {
        // Arrange: PaymentController로 보낼 요청 객체를 구성
        PostPaymentConfirmRequest request = new PostPaymentConfirmRequest();
        request.setOrderId("order123");
        request.setAmount(10000);
        request.setPaymentKey("paymentKey123");
        request.setDepFlightId(1L);
        request.setArrFlightId(2L);
        request.setSeatCount(3L);
        request.setPassengers(List.of( // Passenger 정보 설정
                new FlightPassengerDTO("1993-01-01", "John", Gender.MALE, "Doe", "01012345678"),
                new FlightPassengerDTO("1995-02-02", "Jane", Gender.FEMALE, "Doe", "01087654321")
        ));

        // PaymentService의 confirmPayment 메서드가 PostPaymentConfirmResponse를 반환하도록 Mock 설정
        PostPaymentConfirmResponse response = new PostPaymentConfirmResponse("order123", 20000);
        when(paymentService.confirmPayment(any(PostPaymentConfirmRequest.class))).thenReturn(response);

        // Act & Assert: MockMvc로 POST 요청을 보내고, 예상되는 응답을 검증
        mockMvc.perform(post("/payments/confirm")
                        .contentType(MediaType.APPLICATION_JSON) // 요청의 Content-Type을 JSON으로 설정
                        .content(""" 
                {
                  "orderId": "order123",
                  "amount": 10000,
                  "paymentKey": "paymentKey123",
                  "depFlightId": 1,
                  "arrFlightId": 2,
                  "seatCount": 3,
                  "passengers": [
                    { "birthDate": "1993-01-01", "firstName": "John", "gender": "MALE", "lastName": "Doe", "phone": "01012345678" },
                    { "birthDate": "1995-02-02", "firstName": "Jane", "gender": "FEMALE", "lastName": "Doe", "phone": "01087654321" }
                  ]
                }
                """)) // JSON 요청 본문
                .andExpect(status().isOk()) // HTTP 200 OK 응답 확인
                .andExpect(jsonPath("$.orderId").value("order123")) // 응답에 포함된 주문 ID 확인
                .andExpect(jsonPath("$.totalAmount").value(20000)) // 응답에 포함된 총 금액 확인
                .andDo(print()); // 요청 및 응답 출력
    }

    // 4. 결제 확인 실패 테스트
    @Test
    void shouldReturnInternalServerErrorWhenPaymentFails() throws Exception {
        // Arrange: PaymentService의 confirmPayment 메서드가 예외를 발생시키도록 Mock 설정
        when(paymentService.confirmPayment(any(PostPaymentConfirmRequest.class)))
                .thenThrow(new RuntimeException("Payment confirmation failed"));

        // Act & Assert: MockMvc로 POST 요청을 보내고, 예상되는 응답을 검증
        mockMvc.perform(post("/payments/confirm")
                        .contentType(MediaType.APPLICATION_JSON) // 요청의 Content-Type을 JSON으로 설정
                        .content("""
                {
                  "orderId": "order123",
                  "amount": 10000,
                  "paymentKey": "paymentKey123",
                  "depFlightId": 1,
                  "arrFlightId": 2,
                  "seatCount": 3,
                  "passengers": [
                    { "birthDate": "1993-01-01", "firstName": "John", "gender": "MALE", "lastName": "Doe", "phone": "01012345678" },
                    { "birthDate": "1995-02-02", "firstName": "Jane", "gender": "FEMALE", "lastName": "Doe", "phone": "01087654321" }
                  ]
                }
                """)) // JSON 요청 본문
                .andExpect(status().isInternalServerError()) // HTTP 500 Internal Server Error 응답 확인
                .andExpect(content().string("Payment confirmation failed")) // 응답 메시지 검증
                .andDo(print()); // 요청 및 응답 출력
    }
}
