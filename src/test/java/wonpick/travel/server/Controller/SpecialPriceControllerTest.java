package wonpick.travel.server.Controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wonpick.travel.server.controller.SpecialPriceController;
import wonpick.travel.server.dto.*;
import wonpick.travel.server.service.FlightService;
import wonpick.travel.server.service.SpecialPriceService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecialPriceController.class) // SpecialPriceController를 대상으로 지정
class SpecialPriceControllerTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc를 사용해 HTTP 요청 및 응답을 테스트

    @MockBean
    private SpecialPriceService specialPriceService; // SpecialPriceService를 Mock 객체로 설정

    @MockBean
    private FlightService flightService; // FlightService를 Mock 객체로 설정

    // 1. 성공적인 특가 리스트 조회 테스트
    @Test
    void shouldReturnSpecialPriceListSuccessfully() throws Exception {
        // Arrange: 테스트에 필요한 데이터 및 Mock 설정
        List<SpecialPriceDTO> mockSpecialPrices = List.of(
                new SpecialPriceDTO(1L, "Special Price 1", "2024-12-01", "Title 1", "Description 1",
                        LocalDateTime.of(2024, 12, 1, 10, 0), LocalDateTime.of(2024, 12, 31, 18, 0),
                        "Category 1", 50000, "image1_url", "ICN", "JFK"),
                new SpecialPriceDTO(2L, "Special Price 2", "2024-12-02", "Title 2", "Description 2",
                        LocalDateTime.of(2024, 12, 2, 10, 0), LocalDateTime.of(2024, 12, 30, 18, 0),
                        "Category 2", 60000, "image2_url", "ICN", "LAX")
        );
        GetSpecialPriceListResponse mockResponse = new GetSpecialPriceListResponse(mockSpecialPrices);

        when(specialPriceService.getAllSpecialPricesWithFlights()).thenReturn(mockResponse); // Mock 설정

        // Act & Assert
        mockMvc.perform(get("/special")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 상태 코드 확인
                .andExpect(jsonPath("$.status").value(200)) // 성공 상태 코드 확인
                .andExpect(jsonPath("$.data.specialPrices[0].id").value(1L)) // 첫 번째 특가 ID 확인
                .andExpect(jsonPath("$.data.specialPrices[0].destination").value("Special Price 1")) // 목적지 확인
                .andExpect(jsonPath("$.data.specialPrices[0].minPrice").value(50000)) // 최소 가격 확인
                .andDo(print()); // 요청 및 응답 로그 출력
    }

    // 2. 특가 리스트 조회 실패 시나리오 테스트
    @Test
    void shouldReturnInternalServerErrorWhenFetchingSpecialPriceFails() throws Exception {
        // Arrange: Mock 설정
        when(specialPriceService.getAllSpecialPricesWithFlights())
                .thenThrow(new RuntimeException("Failed to fetch special prices")); // Mock 예외 설정

        // Act & Assert
        mockMvc.perform(get("/special")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // HTTP 500 상태 코드 확인
                .andExpect(jsonPath("$.code").value("C002")) // 에러 코드 검증
                .andExpect(jsonPath("$.message").value("Failed to fetch special prices")) // 에러 메시지 검증
                .andDo(print()); // 요청 및 응답 로그 출력
    }

    // 3. 성공적인 왕복 항공편 조회 테스트
    @Test
    void shouldReturnRoundTripFlightSuccessfully() throws Exception {
        // Arrange: 테스트에 필요한 데이터 및 Mock 설정
        Long spId = 1L;
        String departureDate = "2024-12-01";
        String arrivalDate = "2024-12-10";
        String depAirportCode = "ICN";
        String arrAirportCode = "JFK";

        List<FlightDTO> outboundFlights = List.of(
                new FlightDTO(1L, "Airline1", "A123", "Seoul", "New York",
                        LocalDateTime.of(2024, 12, 1, 10, 0), LocalDateTime.of(2024, 12, 1, 18, 0),
                        500000, "ICN", "JFK", "20kg", 200L)
        );

        List<FlightDTO> returnFlights = List.of(
                new FlightDTO(2L, "Airline2", "B456", "New York", "Seoul",
                        LocalDateTime.of(2024, 12, 10, 10, 0), LocalDateTime.of(2024, 12, 10, 18, 0),
                        450000, "JFK", "ICN", "20kg", 200L)
        );

        when(flightService.searchFlights(spId, depAirportCode, arrAirportCode, departureDate))
                .thenReturn(outboundFlights); // 가는 항공편 Mock 설정
        when(flightService.searchFlights(spId, arrAirportCode, depAirportCode, arrivalDate))
                .thenReturn(returnFlights); // 오는 항공편 Mock 설정

        // Act & Assert
        mockMvc.perform(get("/special/{spId}", spId)
                        .param("departureDate", departureDate)
                        .param("arrivalDate", arrivalDate)
                        .param("depAirportCode", depAirportCode)
                        .param("arrAirportCode", arrAirportCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 상태 코드 확인
                .andExpect(jsonPath("$.data.outboundFlights[0].flightId").value(1L)) // 가는 항공편 확인
                .andExpect(jsonPath("$.data.returnFlights[0].flightId").value(2L)) // 오는 항공편 확인
                .andDo(print()); // 요청 및 응답 로그 출력
    }

    // 4. 왕복 항공편 조회 실패 시나리오 테스트
    @Test
    void shouldReturnBadRequestWhenFlightDataIsMissing() throws Exception {
        // Arrange: 잘못된 요청 데이터 설정
        Long spId = 1L;
        String departureDate = "2024-12-01";
        String arrivalDate = ""; // 도착 날짜 누락
        String depAirportCode = "ICN";
        String arrAirportCode = "JFK";

        // Act & Assert
        mockMvc.perform(get("/special/{spId}", spId)
                        .param("departureDate", departureDate)
                        .param("arrivalDate", arrivalDate) // 잘못된 값 전달
                        .param("depAirportCode", depAirportCode)
                        .param("arrAirportCode", arrAirportCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // HTTP 400 상태 코드 확인
                .andDo(print()); // 요청 및 응답 로그 출력
    }
}
