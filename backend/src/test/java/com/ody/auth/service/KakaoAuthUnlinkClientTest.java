package com.ody.auth.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ody.auth.config.KakaoConfig;
import com.ody.auth.config.KakaoProperties;
import com.ody.common.exception.OdyServerErrorException;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

@Import({KakaoAuthUnlinkClientErrorHandler.class, KakaoConfig.class})
@RestClientTest(KakaoAuthUnlinkClient.class)
class KakaoAuthUnlinkClientTest {

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private KakaoAuthUnlinkClient kakaoAuthUnlinkClient;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class FakeKakaoProperties {

        @Bean
        public KakaoProperties kakaoProperties() {
            return new KakaoProperties("https://kapi.kakao.com/v1/user/unlink", "admin-key");
        }
    }

    @DisplayName("카카오 연결 끊기 성공")
    @Test
    void unlinkSuccess() throws JsonProcessingException {
        Map<String, Object> response = Map.of("id", 123456789);
        server.expect(requestTo("https://kapi.kakao.com/v1/user/unlink"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

        assertThatCode(() -> kakaoAuthUnlinkClient.unlink("123456789"))
                .doesNotThrowAnyException();
    }

    @DisplayName("카카오 연결 끊기 재시도")
    @Test
    void unlinkWithUnlinkedUser() throws JsonProcessingException {
        Map<String, Object> response = Map.of(
                "msg", "NotRegisteredUserException",
                "code", -101
        );
        server.expect(requestTo("https://kapi.kakao.com/v1/user/unlink"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(response)));

        assertThatCode(() -> kakaoAuthUnlinkClient.unlink("123456789"))
                .doesNotThrowAnyException();
    }

    @DisplayName("카카오 연결 끊기 실패")
    @Test
    void unlinkException() throws JsonProcessingException {
        Map<String, Object> response = Map.of(
                "msg", "IllegalParamException",
                "code", -2
        );
        server.expect(requestTo("https://kapi.kakao.com/v1/user/unlink"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(response)));

        assertThatThrownBy(() -> kakaoAuthUnlinkClient.unlink("123456789"))
                .isInstanceOf(OdyServerErrorException.class);
    }
}
