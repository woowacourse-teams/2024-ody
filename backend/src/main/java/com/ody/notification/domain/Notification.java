package com.ody.notification.domain;

import com.ody.common.domain.BaseEntity;
import com.ody.mate.domain.Mate;
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
    private LocalDateTime sendAt;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private NotificationStatus status;

    public Notification(Mate mate, NotificationType type, LocalDateTime sendAt, NotificationStatus status) {
        this(null, mate, type, sendAt, status);
    }

    public static Notification createEntry(Mate mate) {
        return new Notification(mate, NotificationType.ENTRY, LocalDateTime.now(), NotificationStatus.PENDING);
    }

    public static Notification createDepartureReminder(Mate mate, LocalDateTime sendAt) {
        return new Notification(mate, NotificationType.DEPARTURE_REMINDER, sendAt, NotificationStatus.PENDING);
    }

    public static Notification createNudge(Mate mate) {
        return new Notification(mate, NotificationType.NUDGE, LocalDateTime.now(), NotificationStatus.PENDING);
    }

    public void updateStatusToDone() {
        this.status = NotificationStatus.DONE;
    }
}
