package com.ody.mate.domain;

import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import jakarta.persistence.Column;
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
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.SQLDelete;

@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uniqueMeetingAndMemberAndDeletedAt",
                columnNames = {"meeting_id", "member_id", "deleted_at"}
        )
})
@Entity
@Getter
@Filter(name = "deletedMateFilter", condition = "deleted_at IS NULL")
@FilterDef(name = "deletedMateFilter")
@SQLDelete(sql = "UPDATE mate SET deleted_at = NOW() WHERE id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    @NotNull
    private Nickname nickname;

    @Embedded
    @NotNull
    private Location origin;

    @NotNull
    private long estimatedMinutes;

    @Column(columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime deletedAt;

    public Mate(Meeting meeting, Member member, Nickname nickname, Location origin, long estimatedMinutes) {
        this(null, meeting, member, nickname, origin, estimatedMinutes, null);
    }

    public Mate(Meeting meeting, Member member, Location origin, long estimatedMinutes) {
        this(null, meeting, member, member.getNickname(), origin, estimatedMinutes, null);
    }

    public boolean isAttended(Meeting otherMeeting) {
        return Objects.equals(this.meeting.getId(), otherMeeting.getId());
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
