package com.ody.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        MDC.put("requestId", UUID.randomUUID().toString());

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(
                (HttpServletRequest) servletRequest
        );
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(
                (HttpServletResponse) servletResponse
        );
        filterChain.doFilter(requestWrapper, responseWrapper);
        responseWrapper.copyBodyToResponse();

        MDC.clear();
    }
}
