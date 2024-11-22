package wonpick.travel.server.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wonpick.travel.server.dto.*;
import wonpick.travel.server.dto.BaseResponse;
import wonpick.travel.server.entity.Event;
import wonpick.travel.server.service.EventService;
import wonpick.travel.server.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "http://localhost:5173") // 프론트엔드 주소를 명시
public class EventController {

    private final EventService eventService;
    private final S3Service s3Service;
    private static final Logger logger = LogManager.getLogger(EventController.class);

    @Autowired
    public EventController(EventService eventService, S3Service s3Service) {
        this.eventService = eventService;
        this.s3Service = s3Service;
    }

    // 전체 이벤트 목록 조회
    @GetMapping
    public ResponseEntity<BaseResponse<GetEventListResponse>> getAllEvents() {
        logger.info("[travelwonpick] 이벤트 목록 조회");
        List<EventDTO> eventList = eventService.getAllEvents();
        GetEventListResponse response = new GetEventListResponse();
        response.setEvents(eventList);

        return ResponseEntity.ok(BaseResponse.success(response));
    }

    // ID 기준 상세 게시글 조회
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<EventDetailDTO>> getEventById(@PathVariable Long id) {
        logger.info("[travelwonpick] 상세 게시글 조회");
        try {
            EventDetailDTO eventDetail = eventService.getEventById(id);
            return ResponseEntity.ok(BaseResponse.success(eventDetail));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.failure("Event not found: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    // 게시글 생성
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<BaseResponse<PostEventResponse>> createEvent(
            @ModelAttribute PostEventRequest request) {
        logger.info("[travelwonpick] 게시글 생성");
        try {
            // S3에 이미지 파일 업로드
            String imageUrl = s3Service.uploadFile(request.getImage());
            String previewImageUrl = s3Service.uploadFile(request.getPreviewImage());

            // Event 엔티티 생성
            Event event = Event.builder()
                    .title(request.getTitle())
                    .image(imageUrl)                // S3에서 받은 이미지 URL 설정
                    .previewImage(previewImageUrl)  // S3에서 받은 미리보기 이미지 URL 설정
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .build();

            Event createdEvent = eventService.createEvent(event);

            // 성공 응답
            PostEventResponse responseDTO = new PostEventResponse(createdEvent.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponse.success(responseDTO));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.failure("Failed to create event: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }


    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteEvent(@PathVariable Long id) {
        logger.info("[travelwonpick] 게시글 삭제");
        try {
            eventService.deleteEvent(id);
            // 삭제 성공 응답
            return ResponseEntity.ok(BaseResponse.success(null));
        } catch (Exception e) {
            // 삭제 실패 응답
            String errorMessage = "Failed to delete event: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.failure(errorMessage, HttpStatus.BAD_REQUEST));
        }
    }
}
