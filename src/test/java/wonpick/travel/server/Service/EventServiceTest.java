package wonpick.travel.server.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wonpick.travel.server.dto.EventDTO;
import wonpick.travel.server.dto.EventDetailDTO;
import wonpick.travel.server.entity.Event;
import wonpick.travel.server.repository.EventRepository;
import wonpick.travel.server.service.EventService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito Extension을 사용
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    // 게시글 생성
    @Test
    void shouldCreateEventSuccessfully() {
        // Arrange
        Event event = new Event(
                1L,
                "Test Event",
                "image_url",
                "preview_image_url",
                LocalDateTime.of(2024, 12, 1, 0, 0),
                LocalDateTime.of(2024, 12, 31, 23, 59)
        );
        when(eventRepository.save(event)).thenReturn(event);

        // Act
        Event createdEvent = eventService.createEvent(event);

        // Assert
        assertNotNull(createdEvent);
        assertEquals("Test Event", createdEvent.getTitle());
        verify(eventRepository, times(1)).save(event);
    }

    // 전체 게시글 조회
    @Test
    void shouldReturnAllEvents() {
        // Arrange
        List<Event> events = Arrays.asList(
                new Event(1L, "Event 1", "image1.png", "preview1.png",
                        LocalDateTime.of(2024, 12, 1, 0, 0),
                        LocalDateTime.of(2024, 12, 31, 23, 59)),
                new Event(2L, "Event 2", "image2.png", "preview2.png",
                        LocalDateTime.of(2024, 11, 1, 0, 0),
                        LocalDateTime.of(2024, 11, 30, 23, 59))
        );
        when(eventRepository.findAll()).thenReturn(events);

        // Act
        List<EventDTO> eventDTOs = eventService.getAllEvents();

        // Assert
        assertEquals(2, eventDTOs.size());
        assertEquals("Event 1", eventDTOs.get(0).getTitle());
        assertEquals("preview1.png", eventDTOs.get(0).getPreviewImage());
        verify(eventRepository, times(1)).findAll();
    }

    // 상세 게시글 조회 (성공)
    @Test
    void shouldReturnEventById() {
        // Arrange
        Long eventId = 1L;
        Event event = new Event(
                eventId,
                "Event 1",
                "image1.png",
                "preview1.png",
                LocalDateTime.of(2024, 12, 1, 0, 0),
                LocalDateTime.of(2024, 12, 31, 23, 59)
        );
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        EventDetailDTO eventDetail = eventService.getEventById(eventId);

        // Assert
        assertNotNull(eventDetail);
        assertEquals("Event 1", eventDetail.getTitle());
        assertEquals("image1.png", eventDetail.getImage());
        verify(eventRepository, times(1)).findById(eventId);
    }

    // 상세 게시글 조회 (실패)
    @Test
    void shouldThrowExceptionWhenEventNotFound() {
        // Arrange
        Long eventId = 99L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.getEventById(eventId);
        });
        assertEquals("Event not found with id 99", exception.getMessage());
        verify(eventRepository, times(1)).findById(eventId);
    }

    // 게시글 삭제 (성공)
    @Test
    void shouldDeleteEventSuccessfully() {
        // Arrange
        Long eventId = 1L;
        Event event = new Event(
                eventId,
                "Event 1",
                "image1.png",
                "preview1.png",
                LocalDateTime.of(2024, 12, 1, 0, 0),
                LocalDateTime.of(2024, 12, 31, 23, 59)
        );
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        doNothing().when(eventRepository).deleteById(eventId);

        // Act
        eventService.deleteEvent(eventId);

        // Assert
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).deleteById(eventId);
    }

    // 게시글 삭제 (실패)
    @Test
    void shouldThrowExceptionWhenDeletingNonExistentEvent() {
        // Arrange
        Long eventId = 99L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.deleteEvent(eventId);
        });
        assertEquals("Event not found with id 99", exception.getMessage());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(0)).deleteById(anyLong());
    }

}
