package wonpick.travel.server.service;

import wonpick.travel.server.dto.EventDTO;
import wonpick.travel.server.dto.EventDetailDTO;
import wonpick.travel.server.entity.Event;
import wonpick.travel.server.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // 게시글 생성
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    // 전체 게시글 조회
    // 전체 이벤트 목록 조회
    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(event -> {
                    EventDTO dto = new EventDTO();
                    dto.setTitle(event.getTitle());
                    dto.setPreviewImage(event.getPreviewImage());
                    dto.setStartDate(event.getStartDate());
                    dto.setEndDate(event.getEndDate());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ID 기준 상세 이벤트 조회
    public EventDetailDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id " + id));

        EventDetailDTO dto = new EventDetailDTO();
        dto.setTitle(event.getTitle());
        dto.setImage(event.getImage());
        dto.setStartDate(event.getStartDate());
        dto.setEndDate(event.getEndDate());

        return dto;
    }

    // 게시글 삭제
    public void deleteEvent(Long id) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isPresent()) {
            eventRepository.deleteById(id);
        } else {
            throw new RuntimeException("Event not found with id " + id);
        }
    }

}
