package wonpick.travel.server.util;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static LocalDateTime convertToLocalDateTime(String approvedAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME; // ISO 8601 포맷
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(approvedAt, formatter); // ZonedDateTime으로 파싱
        return zonedDateTime.toLocalDateTime(); // LocalDateTime으로 변환
    }
}
