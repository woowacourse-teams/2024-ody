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
        CachedRequestBodyHttpServletWrapper requestWrapper = getRequestWrapper(request);
        CachedResponseBodyHttpServletWrapper responseWrapper = getResponseWrapper(response);

        if (responseWrapper.getStatus() >= 500) {
            log.error("[Request] {} {}, Query: {}, Headers: {}, Body: {} \n[Response] {}",
                    requestWrapper.getMethod(),
                    requestWrapper.getRequestURI(),
                    requestWrapper.getQueryString(),
                    requestWrapper.getHeader(HttpHeaders.AUTHORIZATION),
                    requestWrapper.getBody(),
                    responseWrapper.getStatus());
        } else if (responseWrapper.getStatus() >= 400) {
            log.warn("[Request] {} {}, Query: {}, Headers: {}, Body: {} \n[Response] {}",
                    requestWrapper.getMethod(),
                    requestWrapper.getRequestURI(),
                    requestWrapper.getQueryString(),
                    requestWrapper.getHeader(HttpHeaders.AUTHORIZATION),
                    requestWrapper.getBody(),
                    responseWrapper.getStatus());
        } else {
            log.info("[Request] {} {}, Query: {}, Headers: {}, Body: {} \n[Response] {}",
                    requestWrapper.getMethod(),
                    requestWrapper.getRequestURI(),
                    requestWrapper.getQueryString(),
                    requestWrapper.getHeader(HttpHeaders.AUTHORIZATION),
                    requestWrapper.getBody(),
                    responseWrapper.getStatus());
        }
    }

    private CachedRequestBodyHttpServletWrapper getRequestWrapper(HttpServletRequest request) throws IOException {
        if (request instanceof CachedRequestBodyHttpServletWrapper) {
            return (CachedRequestBodyHttpServletWrapper) request;
        }
        return new CachedRequestBodyHttpServletWrapper(request);
    }

    private CachedResponseBodyHttpServletWrapper getResponseWrapper(HttpServletResponse response) {
        if (response instanceof CachedRequestBodyHttpServletWrapper) {
            return (CachedResponseBodyHttpServletWrapper) response;
        }
        return new CachedResponseBodyHttpServletWrapper(response);
    }
}
