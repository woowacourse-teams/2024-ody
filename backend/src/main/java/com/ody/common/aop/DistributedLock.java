package com.ody.common.aop;

import jakarta.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    @NotNull
    String key();

    long waitTime() default 3L; // Lock 흭득 대기 시간

    long leaseTime() default 5L; // Lock 보유 시간 (부하 분산 서버 다운 문제 해결을 위한 시간)

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
