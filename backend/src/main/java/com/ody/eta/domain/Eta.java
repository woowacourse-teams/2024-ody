package com.ody.eta.domain;

import com.ody.mate.domain.Mate;
import com.ody.util.TimeUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Eta(Mate mate, Long remainingMinutes) {
        this(
                null,
                mate,
                remainingMinutes,
                false,
                TimeUtil.now(),
                TimeUtil.now()
        );
    }

    public Eta(Mate mate, long remainingMinutes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(null, mate, remainingMinutes, false, createdAt, updatedAt);
    }

    public boolean isModified() {
        return !createdAt.isEqual(updatedAt);
    }

    public boolean willBeLate(LocalDateTime meetingTime) {
        LocalDateTime now = TimeUtil.now();
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
        return Duration.between(updatedAt, TimeUtil.now()).toMinutes();
    }

    public void updateRemainingMinutes(long remainingMinutes) {
        this.updatedAt = TimeUtil.now();
        this.remainingMinutes = remainingMinutes;
    }

    public void updateArrived() {
        this.isArrived = true;
        this.remainingMinutes = 0L;
    }
}
