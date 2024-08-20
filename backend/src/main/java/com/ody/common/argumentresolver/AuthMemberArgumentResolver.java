package com.ody.common.argumentresolver;

import com.ody.auth.service.AuthService;
import com.ody.common.annotation.AuthMember;
import com.ody.common.exception.OdyException;
import com.ody.common.exception.OdyUnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@RequiredArgsConstructor
public class AuthMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthService authService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthMember.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        try {
            return authService.parseAccessToken(webRequest.getHeader(HttpHeaders.AUTHORIZATION));
        } catch (OdyException exception) {
            log.warn(exception.getMessage());
            throw new OdyUnauthorizedException("액세스 토큰이 유효하지 않습니다.");
        }
    }
}

