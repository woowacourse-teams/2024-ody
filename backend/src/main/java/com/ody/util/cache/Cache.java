package com.ody.util.cache;

public interface Cache<T, R> {

    void put(T key, R value);

    R get(T key);

    boolean exists(T key);
}
