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
    private MockMvc mockMvc;

    @MockBean
    private SpecialPriceService specialPriceService;

    @MockBean
    private FlightService flightService;

    // 테스트 메서드 추가
    @Test
    void shouldReturnSpecialPriceListSuccessfully() throws Exception {
        // Arrange
        List<SpecialPriceDTO> mockSpecialPrices = List.of(
                new SpecialPriceDTO(
                        1L,
                        "Special Price 1",
                        "2024-12-01",
                        "Title 1",
                        "Description 1",
                        LocalDateTime.of(2024, 12, 1, 10, 0),
                        LocalDateTime.of(2024, 12, 31, 18, 0),
                        "Category 1",
                        50000, // int 값으로 유지
                        "image1_url",
                        "ICN",
                        "JFK"
                ),
                new SpecialPriceDTO(
                        2L,
                        "Special Price 2",
                        "2024-12-02",
                        "Title 2",
                        "Description 2",
                        LocalDateTime.of(2024, 12, 2, 10, 0),
                        LocalDateTime.of(2024, 12, 30, 18, 0),
                        "Category 2",
                        60000, // int 값으로 유지
                        "image2_url",
                        "ICN",
                        "LAX"
                )
        );
        GetSpecialPriceListResponse mockResponse = new GetSpecialPriceListResponse(mockSpecialPrices);

        when(specialPriceService.getAllSpecialPricesWithFlights()).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/special")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200)) // 성공 상태 코드 확인
                .andExpect(jsonPath("$.data.specialPrices[0].id").value(1L)) // 첫 번째 특가 ID 확인
                .andExpect(jsonPath("$.data.specialPrices[0].destination").value("Special Price 1")) // 첫 번째 목적지 확인
                .andExpect(jsonPath("$.data.specialPrices[0].minPrice").value(50000)) // 첫 번째 가격 확인
                .andDo(print());
    }



    // 실패 시나리오: 특가 리스트 조회 중 서버 에러
    @Test
    void shouldReturnInternalServerErrorWhenFetchingSpecialPriceFails() throws Exception {
        // Arrange: Mock 설정
        when(specialPriceService.getAllSpecialPricesWithFlights())
                .thenThrow(new RuntimeException("Failed to fetch special prices"));

        // Act & Assert
        mockMvc.perform(get("/special")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // HTTP 500 상태 코드 확인
                .andExpect(jsonPath("$.code").value("C002")) // 응답 JSON의 code 확인
                .andExpect(jsonPath("$.message").value("Failed to fetch special prices")) // 응답 메시지 확인
                .andDo(print());
    }



    //성공적인 왕복 항공편 조회
    @Test
    void shouldReturnRoundTripFlightSuccessfully() throws Exception {
        // Arrange
        Long spId = 1L;
        String departureDate = "2024-12-01";
        String arrivalDate = "2024-12-10";
        String depAirportCode = "ICN";
        String arrAirportCode = "JFK";

        List<FlightDTO> outboundFlights = List.of(
                new FlightDTO(1L, "Airline1", "A123", "Seoul", "New York",
                        LocalDateTime.of(2024, 12, 1, 10, 0),
                        LocalDateTime.of(2024, 12, 1, 18, 0),
                        500000, "ICN", "JFK", "20kg", 200L)
        );

        List<FlightDTO> returnFlights = List.of(
                new FlightDTO(2L, "Airline2", "B456", "New York", "Seoul",
                        LocalDateTime.of(2024, 12, 10, 10, 0),
                        LocalDateTime.of(2024, 12, 10, 18, 0),
                        450000, "JFK", "ICN", "20kg", 200L)
        );

        when(flightService.searchFlights(spId, depAirportCode, arrAirportCode, departureDate))
                .thenReturn(outboundFlights);
        when(flightService.searchFlights(spId, arrAirportCode, depAirportCode, arrivalDate))
                .thenReturn(returnFlights);

        // Act & Assert
        mockMvc.perform(get("/special/{spId}", spId)
                        .param("departureDate", departureDate)
                        .param("arrivalDate", arrivalDate)
                        .param("depAirportCode", depAirportCode)
                        .param("arrAirportCode", arrAirportCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 성공 상태 코드 확인
                .andExpect(jsonPath("$.data.outboundFlights[0].flightId").value(1L)) // 가는 항공편 검증
                .andExpect(jsonPath("$.data.returnFlights[0].flightId").value(2L)) // 오는 항공편 검증
                .andDo(print());
    }

    //실패 시나리오: 왕복 항공편 조회 중 데이터 누락
    @Test
    void shouldReturnBadRequestWhenFlightDataIsMissing() throws Exception {
        // Arrange
        Long spId = 1L;
        String departureDate = "2024-12-01";
        String arrivalDate = ""; // 도착 날짜 누락
        String depAirportCode = "ICN";
        String arrAirportCode = "JFK";

        // Act & Assert
        mockMvc.perform(get("/special/{spId}", spId)
                        .param("departureDate", departureDate)
                        .param("arrivalDate", arrivalDate) // 빈 값 전달
                        .param("depAirportCode", depAirportCode)
                        .param("arrAirportCode", arrAirportCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // HTTP 400 확인
                .andDo(print());
    }

}