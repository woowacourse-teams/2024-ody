package com.ody.common.interceptor.dto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpHeaders;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

public record RequestResponseWrappers(

        ContentCachingRequestWrapper requestWrapper,
        ContentCachingResponseWrapper responseWrapper
) {

    public RequestResponseWrappers(HttpServletRequest request, HttpServletResponse response) {
        this((ContentCachingRequestWrapper) request, (ContentCachingResponseWrapper) response);
    }

    public String requestAndResponseMessage() {
        return requestMessageWithResponseStatus()
                +
                String.format(", Body: %s", new String(responseWrapper.getContentAsByteArray()));
    }

    public String requestMessageWithResponseStatus() {
        return String.format("[Request] %s %s, Query: %s, Headers: %s, Body: %s [Response] %s",
                requestWrapper.getMethod(),
                requestWrapper.getRequestURI(),
                requestWrapper.getQueryString(),
                requestWrapper.getHeader(HttpHeaders.AUTHORIZATION),
                removeNewlines(new String(requestWrapper.getContentAsByteArray())),
                responseWrapper.getStatus()
        );
    }

    private String removeNewlines(String body) {
        return body.replaceAll("\\n|\\s{2}", "");
    }
}
