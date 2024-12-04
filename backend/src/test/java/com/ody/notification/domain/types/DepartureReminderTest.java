package com.ody.notification.domain.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.ody.common.Fixture;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.route.domain.DepartureTime;
import com.ody.util.TimeUtil;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DepartureReminderTest {

    @DisplayName("출발 알림 생성 시점이 알림 전송 시점보다 늦은 경우 즉시 전송된다")
    @Test
    void sendImmediatelyIfDepartureTimeIsPast() {
        Mate mate = Mockito.mock(Mate.class);
        FcmTopic fcmTopic = Mockito.mock(FcmTopic.class);
        DepartureTime departureTime = Mockito.mock(DepartureTime.class);
        LocalDateTime now = TimeUtil.nowWithTrim();

        Mockito.when(departureTime.isBefore(any()))
                .thenReturn(true);

        DepartureReminder departureReminder = new DepartureReminder(mate, departureTime, fcmTopic);
        Notification departureReminderNotification = departureReminder.toNotification();
        LocalDateTime actualSendTime = TimeUtil.trimSecondsAndNanos(departureReminderNotification.getSendAt());

        assertThat(actualSendTime).isEqualTo(now);
    }

}
