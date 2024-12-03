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
    private MockMvc mockMvc; // MockMvc로 HTTP 요청을 모의

    @MockBean
    private OrderService orderService; // Mock 객체로 OrderService 대체

    // 테스트 메서드 추가
    // 정상 요청
    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        // Arrange
        String authHeader = "Bearer test-access-token";
        PostOrderResponse mockResponse = new PostOrderResponse(123L);

        // Mocking the service call
        when(orderService.saveOrder("test-access-token", "order123", 10000))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/order/create")
                        .header("Authorization", authHeader) // Authorization 헤더 추가
                        .contentType(MediaType.APPLICATION_JSON) // JSON 요청 지정
                        .content("{\"orderId\":\"order123\", \"amount\":10000}")) // 요청 본문
                .andExpect(status().isCreated()) // 201 Created 확인
                .andExpect(jsonPath("$.data.orderSeqId").value(123)) // 응답 데이터 검증
                .andDo(print()); // 요청/응답 로그 출력
    }


    // 예외 처리
    @Test
    void shouldReturnInternalServerErrorWhenSaveOrderFails() throws Exception {
        // Arrange
        String authHeader = "Bearer invalid-token";

        // Mocking the service to throw an exception
        when(orderService.saveOrder("invalid-token", "order123", 10000))
                .thenThrow(new RuntimeException("Invalid access token"));

        // Act & Assert
        mockMvc.perform(post("/order/create")
                        .header("Authorization", authHeader) // Authorization 헤더 추가
                        .contentType(MediaType.APPLICATION_JSON) // JSON 요청 지정
                        .content("{\"orderId\":\"order123\", \"amount\":10000}")) // 요청 본문
                .andExpect(status().isInternalServerError()) // 500 Internal Server Error 확인
                .andExpect(jsonPath("$.message").value("Order 저장에 실패하였습니다. Invalid access token")) // 응답 메시지 검증
                .andDo(print()); // 요청/응답 로그 출력
    }



}
