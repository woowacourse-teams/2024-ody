package com.ody.common.exception;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable exception, Method method, Object... params) {
        log.error("비동기 메서드 에러 : method: {} exception: {}", method.getName(), exception.getMessage());
    }
}
