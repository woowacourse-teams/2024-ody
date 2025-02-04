package com.ody.notification.domain.notice;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Notice {

    private final LocalDateTime sendAt;

    public abstract NoticeType getType();
}
