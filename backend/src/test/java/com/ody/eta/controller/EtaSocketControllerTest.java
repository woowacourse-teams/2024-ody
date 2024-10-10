package com.ody.eta.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import com.ody.auth.service.AuthService;
import com.ody.common.Fixture;
import com.ody.common.exception.OdyNotFoundException;
import com.ody.common.websocket.BaseStompTest;
import com.ody.common.websocket.MessageFrameHandler;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.mate.service.MateService;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.util.TimeCache;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

class EtaSocketControllerTest extends BaseStompTest {

    @MockBean
    private MateService mateService;

    @MockBean
    private MeetingService meetingService;

    @MockBean
    private AuthService authService;

    @SpyBean
    private TimeCache timeCache;

    private MessageFrameHandler<Object> handler;

    @BeforeEach
    void setUp() {
        handler = new MessageFrameHandler<>(Object.class);

        Mockito.when(authService.parseAccessToken(any()))
                .thenReturn(generateStubMember()); //인증 처리를 위한 stub

        MateEtaResponsesV2 stubResponse = new MateEtaResponsesV2(100L, List.of());
        Mockito.when(mateService.findAllMateEtas(any(), any(), any()))
                .thenReturn(stubResponse); //응답 stub 처리
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(authService, mateService);
    }

    @DisplayName("오픈 동시 요청에 대해 위치 호출 함수가 예약된다")
    @Test
    void callEtaMethodWhenStartConnection() throws InterruptedException {
        Mockito.when(timeCache.exists(anyLong())).thenReturn(false);
        Mockito.when(meetingService.findById(anyLong())).thenReturn(Fixture.ODY_MEETING);

        subscribeTopic("coordinates");
        stompSession.send("/publish/open/1", "");

        CompletableFuture<Object> completableFuture = handler.getCompletableFuture();
        assertThatCode(() -> completableFuture.get(10, TimeUnit.SECONDS))
                .doesNotThrowAnyException();
    }

    @DisplayName("약속 시간 이후의 상태 목록 조회 호출 시 disconnect 트리거를 당긴다.")
    @Test
    void triggerDisconnect() throws InterruptedException {
        Mockito.when(timeCache.get(anyLong()))
                .thenReturn(LocalDateTime.now().minusMinutes(10L)) // meeting 시간 (10분 전)
                .thenReturn(LocalDateTime.now()); //trigger 당긴지 0초 > 새로 예약 x

        subscribeTopic("disconnect");
        sendEtaRequest();

        CompletableFuture<Object> completableFuture = handler.getCompletableFuture();
        assertThatCode(() -> completableFuture.get(10, TimeUnit.SECONDS))
                .doesNotThrowAnyException();
    }

    @DisplayName("호출 한지 10초가 지난 경우, 10초 뒤 update 요청을 예약한다.")
    @Test
    void scheduleTriggerWhenDurationMoreThan10Seconds() throws InterruptedException {
        Mockito.when(timeCache.get(anyLong()))
                .thenReturn(LocalDateTime.now().plusMinutes(10L)) // meeting 시간 (10분 뒤)
                .thenReturn(LocalDateTime.now().minusSeconds(11L)); //trigger 당긴지 11초 > 새로 예약 O

        subscribeTopic("coordinates");
        sendEtaRequest();

        CompletableFuture<Object> completableFuture = handler.getCompletableFuture();
        assertThatCode(() -> completableFuture.get(15, TimeUnit.SECONDS))
                .doesNotThrowAnyException();
    }

    @DisplayName("호출 한지 10초가 안 되었을 경우, update 요청을 예약하지 않는다.")
    @Test
    void notScheduleTriggerWhenDurationLessThan10Seconds() throws InterruptedException {
        Mockito.when(timeCache.get(anyLong()))
                .thenReturn(LocalDateTime.now().plusMinutes(10L)) // meeting 시간 (10분 뒤)
                .thenReturn(LocalDateTime.now()); //trigger 당긴지 0초 > 새로 예약 x

        subscribeTopic("coordinates");
        sendEtaRequest();

        MateEtaRequest request = new MateEtaRequest(false, "37.515298", "127.103113");
        stompSession.send("/publish/etas/1", request);

        CompletableFuture<Object> completableFuture = handler.getCompletableFuture();
        assertThatThrownBy(() -> completableFuture.get(15, TimeUnit.SECONDS))
                .isInstanceOf(TimeoutException.class);
    }

    @DisplayName("/topic/etas/{meetingId}에 구독한 사람들이 요청을 받는다")
    @Test
    void subscribe() throws ExecutionException, InterruptedException, TimeoutException {
        MateEtaResponsesV2 response = new MateEtaResponsesV2(100L, List.of());

        MessageFrameHandler<MateEtaResponsesV2> handler = new MessageFrameHandler<>(MateEtaResponsesV2.class);
        stompSession.subscribe("/topic/etas/1", handler);
        Thread.sleep(3000);

        Mockito.when(timeCache.get(anyLong()))
                .thenReturn(LocalDateTime.now().plusMinutes(10L)) // meeting 시간 (10분 뒤)
                .thenReturn(LocalDateTime.now()); //trigger 당긴지 0초 > 새로 예약 x

        sendEtaRequest();

        MateEtaResponsesV2 mateEtaResponsesV2 = handler.getCompletableFuture().get(10, TimeUnit.SECONDS);
        assertThat(mateEtaResponsesV2.requesterMateId()).isEqualTo(response.requesterMateId());
    }

    @DisplayName("controller 진입 후 에러 발생 시 /user/queue/errors 로 에러 메시지를 받는다")
    @ParameterizedTest
    @MethodSource("provideExceptionAndExpectedProblemDetail")
    void exceptionHandling(Throwable exception, ProblemDetail expectedErrorMessage)
            throws ExecutionException, InterruptedException, TimeoutException {
        MessageFrameHandler<ProblemDetail> handler = new MessageFrameHandler<>(ProblemDetail.class);

        Mockito.doThrow(exception).when(mateService).findAllMateEtas(any(), any(), any());

        stompSession.subscribe("/user/queue/errors", handler); // 에러 수신용 구독
        Thread.sleep(3000);

        Mockito.when(timeCache.get(anyLong()))
                .thenReturn(LocalDateTime.now().plusMinutes(10L)) // meeting 시간 (10분 뒤)
                .thenReturn(LocalDateTime.now()); //trigger 당긴지 0초 > 새로 예약 x

        sendEtaRequest();

        ProblemDetail actualErrorMessage = handler.getCompletableFuture().get(10, TimeUnit.SECONDS);

        assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);
    }

    public static Stream<Arguments> provideExceptionAndExpectedProblemDetail() {
        return Stream.of(
                Arguments.of(
                        new OdyNotFoundException("not found"),
                        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "not found")
                ),
                Arguments.of(
                        new RuntimeException(),
                        ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러")
                )
        );
    }

    private void sendEtaRequest() throws InterruptedException {
        MateEtaRequest request = new MateEtaRequest(false, "37.515298", "127.103113");
        stompSession.send("/publish/etas/1", request);
        Thread.sleep(1000);
    }

    private void subscribeTopic(String topic) throws InterruptedException {
        stompSession.subscribe("/topic/" + topic + "/1", handler);
        Thread.sleep(1000);
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
