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
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    // 테스트 메서드 추가
    @Test
    void shouldValidatePaymentSuccessfully() throws Exception {
        // Arrange
        when(paymentService.validatePaymentInfo("order123", 10000)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/payments/validate")
                        .contentType(MediaType.APPLICATION_JSON) // JSON 요청 지정
                        .content("{\"orderId\":\"order123\", \"amount\":10000}")) // 요청 본문
                .andExpect(status().isOk()) // 200 OK 확인
                .andExpect(jsonPath("$.data.valid").value(true)) // 결제 인증 성공 확인
                .andExpect(jsonPath("$.data.message").value("결제 정보 인증 성공")) // 메시지 검증
                .andDo(print()); // 요청/응답 정보 출력
    }

    @Test
    void shouldReturnBadRequestForInvalidPayment() throws Exception {
        // Arrange
        when(paymentService.validatePaymentInfo("order123", 10000)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/payments/validate")
                        .contentType(MediaType.APPLICATION_JSON) // JSON 요청 지정
                        .content("{\"orderId\":\"order123\", \"amount\":10000}")) // 요청 본문
                .andExpect(status().isBadRequest()) // 400 Bad Request 확인
                .andExpect(jsonPath("$.message").value("결제 정보가 유효하지 않습니다")) // 메시지 검증
                .andDo(print()); // 요청/응답 정보 출력
    }

    // 결제 성공
    @Test
    void shouldConfirmPaymentSuccessfully() throws Exception {
        // Arrange
        PostPaymentConfirmRequest request = new PostPaymentConfirmRequest();
        request.setOrderId("order123");
        request.setAmount(10000);
        request.setPaymentKey("paymentKey123");
        request.setDepFlightId(1L);
        request.setArrFlightId(2L);
        request.setSeatCount(3L);
        request.setPassengers(List.of(
                new FlightPassengerDTO("1993-01-01", "John", Gender.MALE, "Doe", "01012345678"),
                new FlightPassengerDTO("1995-02-02", "Jane", Gender.FEMALE, "Doe", "01087654321")
        ));

        PostPaymentConfirmResponse response = new PostPaymentConfirmResponse("order123", 20000); // Integer 타입으로 수정

        when(paymentService.confirmPayment(any(PostPaymentConfirmRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/payments/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
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
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("order123"))
                .andExpect(jsonPath("$.totalAmount").value(20000)) // 응답 데이터 검증
                .andDo(print());
    }


    // 결제 실패
    @Test
    void shouldReturnInternalServerErrorWhenPaymentFails() throws Exception {
        // Arrange
        when(paymentService.confirmPayment(any(PostPaymentConfirmRequest.class)))
                .thenThrow(new RuntimeException("Payment confirmation failed"));

        // Act & Assert
        mockMvc.perform(post("/payments/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
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
                """))
                .andExpect(status().isInternalServerError()) // HTTP 500 확인
                .andExpect(content().string("Payment confirmation failed")) // 에러 메시지 검증
                .andDo(print());
    }

}