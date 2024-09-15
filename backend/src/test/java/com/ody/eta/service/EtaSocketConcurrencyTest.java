package com.ody.eta.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ody.common.BaseServiceTest;
import com.ody.common.Fixture;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.mate.service.MateService;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class EtaSocketConcurrencyTest extends BaseServiceTest {

    @SpyBean
    private SocketMessageSender socketMessageSender;

    @MockBean
    private MateService mateService;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private EtaSocketService etaSocketService;

    @AfterEach
    void tearDown() {
        Mockito.reset(socketMessageSender);
    }

    @DisplayName("참여자들이 오픈 요청을 동시에 요청해도 위치 호출 트리거는 1번만 예약된다")
    @Test
    void reserveCoordinatesTriggerOnceWhenOpen() throws InterruptedException {
        Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING);

        int threadNums = 10;
        doNothing().when(socketMessageSender).reserveMessage(anyString(), any(LocalDateTime.class));
        runJobConcurrently(threadNums, () -> etaSocketService.open(meeting.getId()));

        Thread.sleep(1000); //멀티 스레드 완료 대기 -> 생략하면 검증이 먼저 실행되어 에러

        verify(socketMessageSender, times(1))
                .reserveMessage(anyString(), any(LocalDateTime.class));
    }

    @DisplayName("참여자들이 상태 목록 조회 요청을 동시에 요청해도 위치 호출 트리거는 1번만 예약된다")
    @Test
    void reserveCoordinatesTriggerOnceWhenEtaUpdate() throws InterruptedException {
        Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING);

        doNothing().doNothing()
                .when(socketMessageSender)
                .reserveMessage(anyString(), any(LocalDateTime.class));

        etaSocketService.open(meeting.getId());
        Thread.sleep(10000); //최초 호출 10초 뒤

        MateEtaRequest request = new MateEtaRequest(false, "37.515298", "127.103113");

        int threadNums = 10;
        runJobConcurrently(threadNums, () -> etaSocketService.etaUpdate(meeting.getId(), Fixture.MEMBER1, request));

        Thread.sleep(1000); //멀티 스레드 완료 대기 -> 생략하면 검증이 먼저 실행되어 에러

        verify(socketMessageSender, times(2)) // 최초 open 예약(1번) + 10번 동시 호출(1번)
                .reserveMessage(anyString(), any(LocalDateTime.class));
    }

    private void runJobConcurrently(int threadNums, Runnable runnable) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadNums);
        CountDownLatch signal = new CountDownLatch(threadNums);

        for (int i = 0; i < threadNums; i++) {
            executorService.execute(runnable);
            signal.countDown();
        }

        signal.await(); //signal이 0이 되면 넘어감
        executorService.shutdown();
    }
}
