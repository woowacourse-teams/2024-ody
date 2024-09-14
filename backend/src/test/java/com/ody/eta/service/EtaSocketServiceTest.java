package com.ody.eta.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ody.common.BaseServiceTest;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.mate.service.MateService;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.util.cache.Cache;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class EtaSocketServiceTest extends BaseServiceTest {

    @MockBean
    private SocketMessageSender socketMessageSender;

    @MockBean
    private Cache<Long, LocalDateTime> timeCache;

    @MockBean
    private MateService mateService;

    @Autowired
    private EtaSocketService etaSocketService;

    private ArgumentCaptor<String> argumentCaptor;

    @BeforeEach
    void setUp() {
        argumentCaptor = ArgumentCaptor.forClass(String.class);
        MateEtaResponsesV2 stubResponse = new MateEtaResponsesV2(100L, List.of());
        Mockito.when(mateService.findAllMateEtas(any(), any(), any())).thenReturn(stubResponse); //응답 stub 처리
    }

    @AfterEach
    void tearDown(){
        Mockito.reset(mateService);
    }

    @DisplayName("오픈 호출 시 1초 뒤에 위치 호출 함수가 호출된다")
    @Test
    void callEtaMethodWhenStartConnection() {
        Mockito.when(timeCache.exists(anyLong())).thenReturn(true);
        doNothing().when(socketMessageSender).reserveMessage(argumentCaptor.capture(), any(LocalDateTime.class));

        etaSocketService.open(1L);

        verify(socketMessageSender, times(1)).reserveMessage(anyString(), any(LocalDateTime.class));
        assertThat(argumentCaptor.getValue()).isEqualTo("/topic/coordinates/1");
    }

    @DisplayName("약속 시간 이후 상태 목록 조회 호출 시 disconnect 트리거를 당긴다.")
    @Test
    void triggerDisconnect() {
        Mockito.when(timeCache.get(anyLong()))
                .thenReturn(LocalDateTime.now().minusMinutes(10L)) // meeting 시간 (10분 전)
                .thenReturn(LocalDateTime.now()); //trigger 당긴지 0초 > 새로 예약 x

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        doNothing().when(socketMessageSender).sendMessage(argumentCaptor.capture());

        MateEtaRequest request = new MateEtaRequest(false, "37.515298", "127.103113");
        etaSocketService.etaUpdate(1L, generateStubMember(), request);

        verify(socketMessageSender, times(1)).sendMessage(anyString());
        assertThat(argumentCaptor.getValue()).isEqualTo("/topic/disconnect/1");
    }

    @DisplayName("호출 한지 10초가 지난 경우, 다시 위치 호출 요청을 예약한다.")
    @Test
    void scheduleTriggerWhenDurationMoreThan10Seconds() throws InterruptedException {
        Mockito.when(timeCache.get(anyLong()))
                .thenReturn(LocalDateTime.now().plusMinutes(10L)) // meeting 시간 (10분 뒤)
                .thenReturn(LocalDateTime.now().minusSeconds(10L)); //trigger 당긴지 11초 > 새로 예약 O

        MateEtaRequest request = new MateEtaRequest(false, "37.515298", "127.103113");

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        doNothing().when(socketMessageSender).reserveMessage(argumentCaptor.capture(), any(LocalDateTime.class));

        verify(socketMessageSender, times(1)).reserveMessage(anyString(), any(LocalDateTime.class));
        assertThat(argumentCaptor.getValue()).isEqualTo("/topic/coordinates/1");
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
