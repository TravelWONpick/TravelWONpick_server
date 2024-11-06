package wonpick.travel.server.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
public class BaseEntity {

    private Timestamp createdAt;
    private Timestamp updatedAt;

    @PrePersist
    public void prePersist() {
        ZonedDateTime kst = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        this.createdAt = Timestamp.valueOf(kst.toLocalDateTime());
        this.updatedAt = Timestamp.valueOf(kst.toLocalDateTime());
    }

    @PreUpdate
    public void preUpdate() {
        ZonedDateTime kst = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        this.updatedAt = Timestamp.valueOf(kst.toLocalDateTime());
    }
}
