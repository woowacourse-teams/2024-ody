package com.ody.eta.domain;

import com.ody.common.domain.BaseEntity;
import com.ody.mate.domain.Mate;
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
public class Eta extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "mate_id")
    private Mate mate;

    private long remainingMinutes;

    private boolean isArrived;

    public Eta(Mate mate, Long remainingMinutes) {
        this(null, mate, remainingMinutes, false);
    }

    public void updateRemainingMinutes(long remainingMinutes) {
        this.remainingMinutes = remainingMinutes;
    }

    public boolean willBeLate(LocalDateTime meetingTime) {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        long countdownMinutes = countDownMinutes(now);
        LocalDateTime eta = now.plusMinutes(countdownMinutes);
        return eta.isAfter(meetingTime);
    }

    public long countDownMinutes(LocalDateTime localDateTime) {
        long minutesDifference = Duration.between(getUpdatedAt(), localDateTime).toMinutes();
        return Math.max(remainingMinutes - minutesDifference, 0);
    }

    public long differenceMinutesFromLastUpdated() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        return Duration.between(getUpdatedAt(), now).toMinutes();
    }

    public void updateArrived() {
        this.isArrived = true;
    }

    public boolean isModified() {
        return !getCreatedAt().isEqual(getUpdatedAt());
    }
}
