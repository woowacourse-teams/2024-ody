package com.ody.eta.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ody.auth.service.AuthService;
import com.ody.common.Fixture;
import com.ody.common.websocket.BaseStompTest;
import com.ody.common.websocket.MessageFrameHandler;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.mate.service.MateService;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;

class EtaSocketControllerTest extends BaseStompTest {

    @MockBean
    private MateService mateService;

    @MockBean
    private TaskScheduler taskScheduler;

    @MockBean
    private AuthService authService;

    @SpyBean
    private SimpMessagingTemplate template;

    @Autowired
    private MeetingRepository meetingRepository;

    @DisplayName("/publish/open/{meetingId} 호출 시 1초 뒤에 위치 호출 함수가 예약된다")
    @Test
    void callEtaMethodWhenStartConnection() throws InterruptedException {
        Meeting meeting = generateNotOverdueMeeting();

        Mockito.when(taskScheduler.schedule(any(Runnable.class), any(Instant.class)))
                .thenReturn(null);
        this.stompSession.send("/publish/open/" + meeting.getId(), "");

        /* THEN */
        Thread.sleep(3000);
        verify(taskScheduler, times(1))
                .schedule(any(Runnable.class), any(Instant.class)); //TODO: 10 초 내 n번 open이 될때 스케쥴링은 1번만 되는지
    }

    @DisplayName("약속 시간 이후, /publish/etas/{meetingId} 호출 시 disconnect 트리거를 당긴다.")
    @Test
    void triggerDisconnect() throws InterruptedException, ExecutionException, TimeoutException {
        MateEtaResponsesV2 stubResponse = new MateEtaResponsesV2(100L, List.of());

        Mockito.when(authService.parseAccessToken(any()))
                .thenReturn(generateStubMember()); //인증 처리를 위한 stub

        Mockito.when(mateService.findAllMateEtas(any(), any(), any()))
                .thenReturn(stubResponse); //응답 stub 처리

        Mockito.when(taskScheduler.schedule(any(Runnable.class), any(Instant.class)))
                .thenReturn(null);

        Meeting notOverdueMeeting = generateNotOverdueMeeting();
        Meeting overdueMeeting = generateOverdueMeeting();
        MateEtaRequest request = new MateEtaRequest(false, "37.515298", "127.103113");

        MessageFrameHandler<MateEtaResponsesV2> handler = new MessageFrameHandler<>(MateEtaResponsesV2.class);
        stompSession.subscribe("/topic/etas/" + overdueMeeting.getId(), handler);
        Thread.sleep(3000);

        stompSession.send("/publish/open/" + overdueMeeting.getId(), "");
        Thread.sleep(3000);

        stompSession.send("/publish/etas/" + overdueMeeting.getId(), request);

        Thread.sleep(3000);
        handler.getCompletableFuture().get(10, TimeUnit.SECONDS);
        verify(template, times(1))
                .convertAndSend(eq("/topic/disconnect/" + overdueMeeting.getId()), eq(""));
    }

    @DisplayName("호출 한지 10초가 지난 경우, 다시 update 요청을 예약한다.")
    @Test
    void scheduleTriggerWhenDurationMoreThan10Seconds()
            throws InterruptedException, ExecutionException, TimeoutException {

        MateEtaResponsesV2 stubResponse = new MateEtaResponsesV2(100L, List.of());

        Mockito.when(authService.parseAccessToken(any()))
                .thenReturn(generateStubMember()); //인증 처리를 위한 stub

        Mockito.when(mateService.findAllMateEtas(any(), any(), any()))
                .thenReturn(stubResponse); //응답 stub 처리

        Meeting notOverdueMeeting = generateNotOverdueMeeting();
        Meeting overdueMeeting = generateOverdueMeeting();
        MateEtaRequest request = new MateEtaRequest(false, "37.515298", "127.103113");

        Mockito.when(taskScheduler.schedule(any(Runnable.class), any(Instant.class)))
                .thenReturn(null)
                .thenReturn(null);

        Thread.sleep(3000);

        stompSession.send("/publish/open/" + notOverdueMeeting.getId(), "");
        Thread.sleep(3000);

        stompSession.send("/publish/etas/" + notOverdueMeeting.getId(), request);

        Thread.sleep(11000); //TODO: 10초전 트리거 타임을 셋팅해두고, 10초 뒤 실제 요청 보냈을 때 update 되는지 확인
        stompSession.send("/publish/etas/" + notOverdueMeeting.getId(), request);

        Thread.sleep(3000);
        verify(taskScheduler, times(2))
                .schedule(any(Runnable.class), any(Instant.class));
    }
//    }

    @DisplayName("/topic/etas/{meetingId}에 구독한 사람들이 요청을 받는다")
    @Test
    void subscribe() throws ExecutionException, InterruptedException, TimeoutException {
        Meeting notOverdueMeeting = generateNotOverdueMeeting();
        MateEtaRequest request = new MateEtaRequest(false, "37.515298", "127.103113");
        MateEtaResponsesV2 response = new MateEtaResponsesV2(100L, List.of());

        MessageFrameHandler<MateEtaResponsesV2> handler = new MessageFrameHandler<>(MateEtaResponsesV2.class);
        stompSession.subscribe("/topic/etas/" + notOverdueMeeting.getId(), handler);

        Thread.sleep(3000);

        Mockito.when(authService.parseAccessToken(any())).thenReturn(generateStubMember()); //인증 처리를 위한 stub
        Mockito.when(mateService.findAllMateEtas(any(), any(), any())).thenReturn(response); //응답 stub 처리
        Mockito.when(taskScheduler.schedule(any(Runnable.class), any(Instant.class))).thenReturn(null);

        stompSession.send("/publish/open/" + notOverdueMeeting.getId(), "");
        stompSession.send("/publish/etas/" + notOverdueMeeting.getId(), request);

        Thread.sleep(3000);

        MateEtaResponsesV2 mateEtaResponsesV2 = handler.getCompletableFuture().get(10, TimeUnit.SECONDS);
        assertThat(mateEtaResponsesV2.requesterMateId()).isEqualTo(response.requesterMateId());
    }

    private Meeting generateOverdueMeeting() {
        LocalDateTime past = LocalDateTime.now().minusMinutes(10L);

        Meeting meeting = new Meeting(
                "오디1",
                past.toLocalDate(),
                past.toLocalTime(),
                Fixture.TARGET_LOCATION,
                "초대코드1"
        );

        return meetingRepository.save(meeting);
    }

    private Meeting generateNotOverdueMeeting() {
        LocalDateTime future = LocalDateTime.now().plusMinutes(10L);

        Meeting meeting = new Meeting(
                "오디2",
                future.toLocalDate(),
                future.toLocalTime(),
                Fixture.TARGET_LOCATION,
                "초대코드2"
        );

        return meetingRepository.save(meeting);
    }

    private Member generateStubMember() {
        return new Member(
                1L,
                new AuthProvider("pid"),
                "콜리1",
                "imageUrl1",
                new DeviceToken("dt1"),
                null,
                null
        );
    }
}
