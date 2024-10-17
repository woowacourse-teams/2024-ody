package com.ody.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Aspect
@Component
@Profile("!prod")
@RestController
public class ExecutionTimeAspect {

    @Pointcut("within(com.ody..controller..*)")
    public void controllerMethod() {
    }

    @Pointcut("@annotation(ExecutionTimer)")
    public void methodWithExecutionTimer() {
    }

    @Around("controllerMethod()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();
            log.info(
                    "[API Execution Time] - {} : {}ms",
                    joinPoint.getSignature().toShortString(),
                    stopWatch.getTotalTimeMillis()
            );
        }
    }
}
