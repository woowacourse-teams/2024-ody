package com.ody.eta.domain;

import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
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

    private boolean isMissing;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Eta(Mate mate, Long remainingMinutes) {
        this(
                null,
                mate,
                remainingMinutes,
                false,
                false,
                TimeUtil.nowWithTrim(),
                TimeUtil.nowWithTrim()
        );
    }

    public Eta(Mate mate, long remainingMinutes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(null, mate, remainingMinutes, false, false, createdAt, updatedAt);
    }

    public boolean isModified() {
        return !createdAt.isEqual(updatedAt);
    }

    public long countDownMinutes() {
        LocalDateTime now = TimeUtil.nowWithTrim();
        long minutesDifference = Duration.between(updatedAt, now).toMinutes();
        return Math.max(remainingMinutes - minutesDifference, 0);
    }

    public boolean isArrivalSoon(Meeting meeting) {
        LocalDateTime now = TimeUtil.nowWithTrim();
        LocalDateTime eta = now.plusMinutes(countDownMinutes());
        return (eta.isBefore(meeting.getMeetingTime()) || eta.isEqual(meeting.getMeetingTime())) && !isArrived;
    }

    public long differenceMinutesFromLastUpdated() {
        return Duration.between(updatedAt, TimeUtil.nowWithTrim()).toMinutes();
    }

    public void updateRemainingMinutes(long remainingMinutes) {
        this.updatedAt = TimeUtil.nowWithTrim();
        this.remainingMinutes = remainingMinutes;
    }

    public void updateArrived() {
        this.isArrived = true;
        this.remainingMinutes = 0L;
    }

    public void updateMissingBy(boolean isMissing) {
        this.isMissing = isMissing;
    }
}
