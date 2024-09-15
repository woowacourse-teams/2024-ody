package com.ody.util;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class TimeCache {

    private final Map<Long, LocalDateTime> timeCache = new ConcurrentHashMap<>();

    public void put(Long key, LocalDateTime value) {
        timeCache.put(key, value);
    }

    public LocalDateTime get(Long key) {
        return timeCache.get(key);
    }

    public boolean exists(Long key) {
        return timeCache.containsKey(key);
    }
}
