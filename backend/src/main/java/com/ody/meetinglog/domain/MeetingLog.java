package com.ody.meetinglog.domain;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class MeetingLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @JoinColumn(name = "mate_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Mate mate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MeetingLogType type;

    public MeetingLog(Mate mate, MeetingLogType type) {
        this(null, mate, type);
    }
}
