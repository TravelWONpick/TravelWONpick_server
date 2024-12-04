package wonpick.travel.server.Controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wonpick.travel.server.controller.OrderController;
import wonpick.travel.server.dto.PostOrderRequest;
import wonpick.travel.server.dto.PostOrderResponse;
import wonpick.travel.server.service.OrderService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(OrderController.class) // OrderController를 테스트 대상으로 지정
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc를 사용해 HTTP 요청 및 응답을 테스트

    @MockBean
    private OrderService orderService; // Mock 객체로 OrderService를 대체하여 독립적인 테스트 가능

    // 1. 정상적인 주문 생성 요청
    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        // Arrange: 테스트에 필요한 데이터 및 Mock 설정
        String authHeader = "Bearer test-access-token"; // Authorization 헤더 설정
        PostOrderResponse mockResponse = new PostOrderResponse(123L); // 서비스 응답 Mock 데이터

        // OrderService의 saveOrder 메서드가 mockResponse를 반환하도록 Mock 설정
        when(orderService.saveOrder("test-access-token", "order123", 10000))
                .thenReturn(mockResponse);

        // Act & Assert: MockMvc를 사용해 HTTP POST 요청을 보내고 응답 검증
        mockMvc.perform(post("/order/create") // POST 요청을 "/order/create" 엔드포인트로 전송
                        .header("Authorization", authHeader) // Authorization 헤더 추가
                        .contentType(MediaType.APPLICATION_JSON) // 요청의 Content-Type을 JSON으로 설정
                        .content("{\"orderId\":\"order123\", \"amount\":10000}")) // 요청 본문(JSON 형식)
                .andExpect(status().isCreated()) // HTTP 201 Created 응답 확인
                .andExpect(jsonPath("$.data.orderSeqId").value(123)) // 응답 데이터의 orderSeqId 값 검증
                .andDo(print()); // 요청 및 응답 로그 출력
    }

    // 2. 주문 생성 실패 시 예외 처리
    @Test
    void shouldReturnInternalServerErrorWhenSaveOrderFails() throws Exception {
        // Arrange: 테스트에 필요한 데이터 및 Mock 설정
        String authHeader = "Bearer invalid-token"; // 잘못된 Authorization 헤더 설정

        // OrderService의 saveOrder 메서드 호출 시 RuntimeException을 발생시키도록 Mock 설정
        when(orderService.saveOrder("invalid-token", "order123", 10000))
                .thenThrow(new RuntimeException("Invalid access token"));

        // Act & Assert: MockMvc를 사용해 HTTP POST 요청을 보내고 응답 검증
        mockMvc.perform(post("/order/create") // POST 요청을 "/order/create" 엔드포인트로 전송
                        .header("Authorization", authHeader) // Authorization 헤더 추가
                        .contentType(MediaType.APPLICATION_JSON) // 요청의 Content-Type을 JSON으로 설정
                        .content("{\"orderId\":\"order123\", \"amount\":10000}")) // 요청 본문(JSON 형식)
                .andExpect(status().isInternalServerError()) // HTTP 500 Internal Server Error 응답 확인
                .andExpect(jsonPath("$.message").value("Order 저장에 실패하였습니다. Invalid access token")) // 에러 메시지 검증
                .andDo(print()); // 요청 및 응답 로그 출력
    }
}

