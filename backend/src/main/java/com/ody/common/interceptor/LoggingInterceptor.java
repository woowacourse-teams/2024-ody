package com.ody.common.interceptor;

import com.ody.common.interceptor.dto.RequestResponseWrappers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        RequestResponseWrappers wrappers = new RequestResponseWrappers(request, response);

        if (response.getStatus() >= 500) {
            log.error(wrappers.requestMessageWithResponseStatus());
        } else if (response.getStatus() >= 400) {
            log.warn(wrappers.requestMessageWithResponseStatus());
        } else {
            log.info(wrappers.requestAndResponseMessage());
        }
    }
}
