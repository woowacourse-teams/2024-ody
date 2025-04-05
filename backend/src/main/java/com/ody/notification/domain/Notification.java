package com.ody.notification.domain;

import com.ody.common.domain.BaseEntity;
import com.ody.mate.domain.Mate;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mate_id")
    @NotNull
    private Mate mate;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private NotificationType type;

    @NotNull
    @Column(columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime sendAt;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private NotificationStatus status;

    @Embedded
    private FcmTopic fcmTopic;

    public Notification(
            Mate mate,
            NotificationType type,
            LocalDateTime sendAt,
            NotificationStatus status,
            FcmTopic fcmTopic
    ) {
        this(null, mate, type, sendAt, status, fcmTopic);
    }

    public boolean isDepartureReminder() {
        return type.isDepartureReminder();
    }

    public boolean isStatusDismissed() {
        return status == NotificationStatus.DISMISSED;
    }

    public void updateStatusToDone() {
        this.status = NotificationStatus.DONE;
    }
}
