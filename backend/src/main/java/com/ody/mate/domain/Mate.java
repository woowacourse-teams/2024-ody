package com.ody.mate.domain;

import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uniqueMeetingAndNickname",
                columnNames = {"meeting_id", "nickname"}
        ),
        @UniqueConstraint(
                name = "uniqueMeetingAndMember",
                columnNames = {"meeting_id", "member_id"}
        )
})
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Mate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    @NotNull
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @Embedded
    @NotNull
    private Nickname nickname;

    @Embedded
    @NotNull
    private Location origin;

    public Mate(Meeting meeting, Member member, Nickname nickname, Location origin) {
        this(null, meeting, member, nickname, origin);
    }
}
