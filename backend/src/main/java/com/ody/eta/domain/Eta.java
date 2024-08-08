package com.ody.eta.domain;

import com.ody.mate.domain.Mate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Eta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "mate_id")
    private Mate mate;

    private long remainingMinutes;

    private boolean isArrived;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Eta(Mate mate, Long remainingMinutes) {
        this(null, mate, remainingMinutes, false, LocalDateTime.now(), LocalDateTime.now());
    }

    public Eta(Mate mate, long remainingMinutes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(null, mate, remainingMinutes, false, createdAt, updatedAt);
    }

    public boolean isModified() {
        return !createdAt.isEqual(updatedAt);
    }

    public boolean willBeLate(LocalDateTime meetingTime) {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        long countdownMinutes = countDownMinutes(now);
        LocalDateTime eta = now.plusMinutes(countdownMinutes);
        return eta.isAfter(meetingTime);
    }

    public long countDownMinutes(LocalDateTime localDateTime) {
        long minutesDifference = Duration.between(updatedAt, localDateTime).toMinutes();
        long countDownMinutes = Math.max(remainingMinutes - minutesDifference, 0);
        if (isMissing()) {
            return -1L;
        }
        if (isArrivalSoon(countDownMinutes)) {
            return 1L;
        }
        return countDownMinutes;
    }

    private boolean isMissing() {
        return remainingMinutes == -1L;
    }

    private boolean isArrivalSoon(long countDownMinutes) {
        return countDownMinutes == 0L && !isArrived;
    }

    public long differenceMinutesFromLastUpdated() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        return Duration.between(updatedAt, now).toMinutes();
    }

    public void updateRemainingMinutes(long remainingMinutes) {
        this.updatedAt = LocalDateTime.now().withSecond(0).withNano(0);
        this.remainingMinutes = remainingMinutes;
    }

    public void updateArrived() {
        this.isArrived = true;
        this.remainingMinutes = 0L;
    }
}
