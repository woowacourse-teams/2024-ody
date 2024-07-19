package com.ody.common.argumentresolver;

import com.ody.common.annotaion.AuthMember;
import com.ody.common.exception.OdyException;
import com.ody.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class AuthMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String DEVICE_TOKEN_PREFIX = "device-token=";

    private final MemberService memberService;

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
        String value = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String deviceToken = extractDeviceToken(value);

        return memberService.findByDeviceToken(deviceToken);
    }

    private String extractDeviceToken(String value) {
        if (!value.startsWith(DEVICE_TOKEN_PREFIX)) {
            throw new OdyException("잚못된 토큰 형식입니다.");
        }
        return value.substring(DEVICE_TOKEN_PREFIX.length());
    }
}

