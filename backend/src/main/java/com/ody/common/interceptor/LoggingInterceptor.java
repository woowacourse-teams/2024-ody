package com.ody.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
        ContentCachingResponseWrapper responseWrapper = (ContentCachingResponseWrapper) response;

        if (responseWrapper.getStatus() >= 500) {
            log.error("[Request] {} {}, Query: {}, Headers: {}, Body: {} [Response] {}",
                    requestWrapper.getMethod(),
                    requestWrapper.getRequestURI(),
                    requestWrapper.getQueryString(),
                    requestWrapper.getHeader(HttpHeaders.AUTHORIZATION),
                    new String(requestWrapper.getContentAsByteArray()),
                    responseWrapper.getStatus());
        } else if (responseWrapper.getStatus() >= 400) {
            log.warn("[Request] {} {}, Query: {}, Headers: {}, Body: {} [Response] {}",
                    requestWrapper.getMethod(),
                    requestWrapper.getRequestURI(),
                    requestWrapper.getQueryString(),
                    requestWrapper.getHeader(HttpHeaders.AUTHORIZATION),
                    new String(requestWrapper.getContentAsByteArray()),
                    responseWrapper.getStatus());
        } else {
            log.info("[Request] {} {}, Query: {}, Headers: {}, Body: {} [Response] {}, Body: {}",
                    requestWrapper.getMethod(),
                    requestWrapper.getRequestURI(),
                    requestWrapper.getQueryString(),
                    requestWrapper.getHeader(HttpHeaders.AUTHORIZATION),
                    new String(requestWrapper.getContentAsByteArray()),
                    responseWrapper.getStatus(),
                    new String(responseWrapper.getContentAsByteArray())
            );
        }
    }
}
