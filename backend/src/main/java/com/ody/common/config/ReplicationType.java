package com.ody.common.config;

import com.ody.common.exception.OdyServerErrorException;
import java.util.Arrays;
import java.util.function.BiPredicate;

public enum ReplicationType {

    READ((transactionActive, readOnly) -> !transactionActive || readOnly),
    WRITE((transactionActive, readOnly) -> transactionActive && !readOnly);

    private final BiPredicate<Boolean, Boolean> condition;

    ReplicationType(BiPredicate<Boolean, Boolean> condition) {
        this.condition = condition;
    }
    public static ReplicationType from(boolean transactionActive, boolean readOnly) {
        return Arrays.stream(values())
                .filter(replicationType -> replicationType.condition.test(transactionActive, readOnly))
                .findAny()
                .orElseThrow(() -> new OdyServerErrorException("잘못된 Replication Type 입니다."));
    }
}
