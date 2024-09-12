package com.ody.meeting.domain;

import com.ody.common.domain.BaseEntity;
import com.ody.util.TimeUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Meeting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime time;

    @Embedded
    @NotNull
    private Location target;

    @NotNull
    @Column(columnDefinition = "CHAR(8)", unique = true)
    private String inviteCode;

    @NotNull
    private boolean overdue;

    public Meeting(String name, LocalDate date, LocalTime time, Location target, String inviteCode) {
        this(null, name, date, TimeUtil.trimSecondsAndNanos(time), target, inviteCode, false);
    }

    public boolean isWithinPast24HoursOrLater() {
        LocalDateTime meetingTime = TimeUtil.trimSecondsAndNanos(LocalDateTime.of(date, time));
        LocalDateTime standard = TimeUtil.nowWithTrim().minusHours(24);
        return meetingTime.isAfter(standard) || meetingTime.isEqual(standard);
    }

    public boolean isEnd() {
        return TimeUtil.nowWithTrim().isAfter(getMeetingTime());
    }

    public LocalDateTime getMeetingTime() {
        return TimeUtil.trimSecondsAndNanos(LocalDateTime.of(date, time));
    }

    public String getTargetAddress() {
        return target.getAddress();
    }

    public Coordinates getTargetCoordinates() {
        return target.getCoordinates();
    }

    public String getTargetLatitude() {
        return target.getLatitude();
    }

    public String getTargetLongitude() {
        return target.getLongitude();
    }
}
