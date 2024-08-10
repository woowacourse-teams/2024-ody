package com.ody.meeting.domain;

import com.ody.util.TimeUtil;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Meeting {

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
    private String inviteCode;

    public Meeting(String name, LocalDate date, LocalTime time, Location target, String inviteCode) {
        this(null, name, date, TimeUtil.trim(time), target, inviteCode);
    }

    public void updateInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public LocalDateTime getMeetingTime() {
        return TimeUtil.trim(LocalDateTime.of(date, time));
    }

    public boolean isWithinPast24HoursOrLater() {
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        LocalDateTime standard = TimeUtil.now().minusHours(24);
        return standard.isBefore(dateTime);
    }
}
