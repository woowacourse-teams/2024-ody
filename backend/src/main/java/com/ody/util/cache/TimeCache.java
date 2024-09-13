package com.ody.util.cache;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class TimeCache implements Cache<Long, LocalDateTime> {

    private final Map<Long, LocalDateTime> timeCache = new ConcurrentHashMap<>();

    @Override
    public void put(Long key, LocalDateTime value) {
        timeCache.put(key, value);
    }

    @Override
    public LocalDateTime get(Long key) {
        return timeCache.get(key);
    }

    @Override
    public boolean exists(Long key) {
        return timeCache.containsKey(key);
    }
}
