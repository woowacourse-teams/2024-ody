package com.ody.eta.argumentresolver;

import com.ody.auth.service.AuthService;
import com.ody.common.exception.OdyException;
import com.ody.common.exception.OdyUnauthorizedException;
import com.ody.eta.annotation.WebSocketAuthMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

@Slf4j
@RequiredArgsConstructor
public class WebSocketArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthService authService;

    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(WebSocketAuthMember.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            Message<?> message
    ) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String accessToken = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

        try {
            return authService.parseAccessToken(accessToken);
        } catch (OdyException exception) {
            log.warn(exception.getMessage());
            throw new OdyUnauthorizedException("액세스 토큰이 유효하지 않습니다.");
        }
    }
}
