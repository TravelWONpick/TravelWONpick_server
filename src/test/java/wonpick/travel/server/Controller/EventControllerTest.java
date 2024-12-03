package wonpick.travel.server.Controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import wonpick.travel.server.controller.EventController;
import wonpick.travel.server.dto.EventDTO;
import wonpick.travel.server.dto.EventDetailDTO;
import wonpick.travel.server.service.EventService;
import wonpick.travel.server.service.S3Service;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class) // EventController를 테스트 대상으로 지정
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc로 HTTP 요청을 모의

    @MockBean
    private EventService eventService; // Mock 객체로 EventService 대체

    @MockBean
    private S3Service s3Service; // Mock 객체로 S3Service 대체

    //이벤트 조회 테스트
    @Test
    void shouldReturnEventListWhenGetAllEventsIsCalled() throws Exception {
        // Arrange: Mock 데이터를 설정
        List<EventDTO> mockEvents = List.of(
                new EventDTO(1L, "Event1", "preview1.png",
                        LocalDateTime.of(2024, 12, 1, 0, 0),
                        LocalDateTime.of(2024, 12, 31, 23, 59)),
                new EventDTO(2L, "Event2", "preview2.png",
                        LocalDateTime.of(2024, 11, 1, 0, 0),
                        LocalDateTime.of(2024, 11, 30, 23, 59))
        );
        when(eventService.getAllEvents()).thenReturn(mockEvents);

        // Act & Assert: MockMvc로 요청/응답 검증
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk()) // HTTP 상태 코드가 200인지 검증
                .andExpect(jsonPath("$.data.events[0].title").value("Event1")) // 첫 번째 이벤트 제목 검증
                .andExpect(jsonPath("$.data.events[0].previewImage").value("preview1.png")) // 첫 번째 미리보기 이미지 검증
                .andExpect(jsonPath("$.data.events[0].startDate").value("2024-12-01T00:00:00")) // 날짜 검증
                .andExpect(jsonPath("$.data.events[1].title").value("Event2")) // 두 번째 이벤트 제목 검증
                .andDo(print()); // 요청/응답 디버깅 정보 출력
    }

    // 상세 이벤트 조회 (성공)
    @Test
    void shouldReturnEventDetailsWhenIdExists() throws Exception {
        // Arrange
        Long eventId = 1L;
        EventDetailDTO mockEventDetail = new EventDetailDTO(
                eventId,
                "Event1",
                "image1.png",
                LocalDateTime.of(2024, 12, 1, 0, 0),
                LocalDateTime.of(2024, 12, 31, 23, 59)
        );
        when(eventService.getEventById(eventId)).thenReturn(mockEventDetail);

        // Act & Assert
        mockMvc.perform(get("/events/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(eventId))
                .andExpect(jsonPath("$.data.title").value("Event1"))
                .andExpect(jsonPath("$.data.image").value("image1.png"))
                .andExpect(jsonPath("$.data.startDate").value("2024-12-01T00:00:00"))
                .andExpect(jsonPath("$.data.endDate").value("2024-12-31T23:59:00"))
                .andDo(print());
    }

    // 이벤트 상세 조회 (실패)
    @Test
    void shouldReturnBadRequestWhenEventNotFound() throws Exception {
        // Arrange
        Long eventId = 99L;
        when(eventService.getEventById(eventId)).thenThrow(new RuntimeException("Event not found"));

        // Act & Assert
        mockMvc.perform(get("/events/{id}", eventId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Event not found: Event not found"))
                .andDo(print());
    }




}
