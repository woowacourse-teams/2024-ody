package com.ody.common.aop;

import com.ody.common.exception.OdyException;
import com.ody.common.exception.OdyServerErrorException;
import com.ody.common.redis.RedissonLockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAop {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonLockManager redissonLockManager;

    @Around("@annotation(distributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) {
        String lockName = REDISSON_LOCK_PREFIX + getDynamicValue(joinPoint, distributedLock.key());

        return redissonLockManager.lock(
                () -> proceedWithJoinPoint(joinPoint),
                lockName,
                distributedLock
        );
    }

    private Object proceedWithJoinPoint(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (OdyException exception) {
            throw exception;
        } catch (Throwable throwable) {
            log.error("분산락 작업 처리중 에러 발생 : ", throwable);
            throw new OdyServerErrorException("서버에 장애가 발생했습니다.");
        }
    }

    private String getDynamicValue(ProceedingJoinPoint joinPoint, String key) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < signature.getParameterNames().length; i++) {
            context.setVariable(signature.getParameterNames()[i], joinPoint.getArgs()[i]);
        }

        return parser.parseExpression(key)
                .getValue(context, String.class);
    }
}
