package com.ody.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        CachedRequestBodyHttpServletWrapper requestWrapper = getWrapper(request);

        if (response.getStatus() >= 500) {
            log.error("{} {}, Query: {}, Headers: {}, ({})", request.getMethod(), request.getRequestURI(),
                    request.getQueryString(), request.getHeader(
                            HttpHeaders.AUTHORIZATION), response.getStatus());
        } else if (response.getStatus() >= 400) {
            log.warn("{} {}, Query: {}, Headers: {}, ({})", request.getMethod(), request.getRequestURI(),
                    request.getQueryString(), request.getHeader(
                            HttpHeaders.AUTHORIZATION), response.getStatus());
        } else {
            log.info("[Request] {} {}, Query: {}, Headers: {}, Body: {} \n[Response] {}",
                    requestWrapper.getMethod(),
                    requestWrapper.getRequestURI(),
                    requestWrapper.getQueryString(),
                    requestWrapper.getHeader(HttpHeaders.AUTHORIZATION),
                    requestWrapper.getBody(),
                    response.getStatus());
        }
    }

    private CachedRequestBodyHttpServletWrapper getWrapper(HttpServletRequest request) throws IOException {
        if (request instanceof CachedRequestBodyHttpServletWrapper) {
            return (CachedRequestBodyHttpServletWrapper) request;
        }
        return new CachedRequestBodyHttpServletWrapper(request);
    }
}
