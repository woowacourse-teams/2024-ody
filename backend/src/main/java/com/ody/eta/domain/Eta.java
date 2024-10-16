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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@SQLDelete(sql = "UPDATE eta SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
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

    @NotNull
    private long remainingMinutes;

    @NotNull
    private boolean isArrived;

    @NotNull
    private boolean isMissing;

    @Column(updatable = false)
    @NotNull
    private LocalDateTime firstApiCallAt;

    @NotNull
    private LocalDateTime lastApiCallAt;

    @Column(columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime deletedAt;

    public Eta(Mate mate, Long remainingMinutes) {
        this(
                null,
                mate,
                remainingMinutes,
                false,
                false,
                TimeUtil.nowWithTrim(),
                TimeUtil.nowWithTrim(),
                null
        );
    }

    // TODO: 테스트에서만 쓰이는 생성자
    public Eta(Mate mate, long remainingMinutes, LocalDateTime firstApiCallAt, LocalDateTime lastApiCallAt) {
        this(null, mate, remainingMinutes, false, false, firstApiCallAt, lastApiCallAt, null);
    }

    public boolean isModified() {
        return !firstApiCallAt.isEqual(lastApiCallAt);
    }

    public long countDownMinutes() {
        LocalDateTime now = TimeUtil.nowWithTrim();
        long minutesDifference = Duration.between(lastApiCallAt, now).toMinutes();
        return Math.max(remainingMinutes - minutesDifference, 0);
    }

    public boolean isArrivalSoon(Meeting meeting) {
        LocalDateTime now = TimeUtil.nowWithTrim();
        LocalDateTime eta = now.plusMinutes(countDownMinutes());
        return (eta.isBefore(meeting.getMeetingTime()) || eta.isEqual(meeting.getMeetingTime())) && !isArrived;
    }

    public long differenceMinutesFromLastUpdated() {
        return Duration.between(lastApiCallAt, TimeUtil.nowWithTrim()).toMinutes();
    }

    public void updateRemainingMinutes(long remainingMinutes) {
        this.lastApiCallAt = TimeUtil.nowWithTrim();
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
