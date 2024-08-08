package com.ody.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView
    ) throws Exception {
        log.info("{} {}, Query: {}, Headers: {}, ({})", request.getMethod(), request.getRequestURI(),
                request.getQueryString(), request.getHeader(
                        HttpHeaders.AUTHORIZATION), response.getStatus());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        if (response.getStatus() == 500) {
            log.error("{} {}, Query: {}, Headers: {}, ({})", request.getMethod(), request.getRequestURI(),
                    request.getQueryString(), request.getHeader(
                            HttpHeaders.AUTHORIZATION), response.getStatus());
            return;
        }
        log.warn("{} {}, Query: {}, Headers: {}, ({})", request.getMethod(), request.getRequestURI(),
                request.getQueryString(), request.getHeader(
                        HttpHeaders.AUTHORIZATION), response.getStatus());
    }
}
