package com.ody.common.redis;

import com.ody.common.aop.DistributedLock;
import com.ody.common.exception.OdyServerErrorException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockManager {

    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;

    public <T> T lock(Supplier<T> supplier, String lockName, DistributedLock distributedLock) {
        RLock rLock = redissonClient.getLock(lockName);
        log.debug("[분산락 시작] {} 획득 시도", lockName);

        try {
            acquireLock(rLock, lockName, distributedLock);
            return executeWithTransaction(supplier);
        } catch (InterruptedException exception) {
            log.error("[분산락 오류] {} 획득 중 인터럽트 발생", lockName, exception);
            throw new OdyServerErrorException("서버에 장애가 발생했습니다.");
        } finally {
            releaseLock(rLock, lockName);
        }
    }

    private void acquireLock(RLock lock, String lockName, DistributedLock distributedLock) throws InterruptedException {
        long waitTime = distributedLock.waitTime();
        long leaseTime = distributedLock.leaseTime();
        TimeUnit timeUnit = distributedLock.timeUnit();
        boolean available = lock.tryLock(waitTime, leaseTime, timeUnit);
        if (!available) {
            log.warn("[분산락 획득 실패] {} {}초 대기 후 락 획득 실패", lockName, waitTime);
            throw new OdyServerErrorException("다른 요청을 처리 중 입니다. 잠시 후 다시 시도해주세요.");
        }
        log.debug("[분산락 획득 성공] {} (유효시간: {}초)", lockName, leaseTime);
    }

    private <T> T executeWithTransaction(Supplier<T> supplier) {
        return transactionTemplate.execute(transactionStatus -> supplier.get());
    }

    private void releaseLock(RLock lock, String lockName) {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            log.debug("[분산락 해제] {} 정상 해제", lockName);
        }
    }
}
