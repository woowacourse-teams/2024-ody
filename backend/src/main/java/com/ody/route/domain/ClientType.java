package com.ody.route.domain;

import java.time.LocalDate;
import java.util.function.Function;

public enum ClientType {

    ODSAY(date -> date),
    GOOGLE(date -> date.withDayOfMonth(1)),
    ;

    private final Function<LocalDate, LocalDate> resetStrategy;

    ClientType(Function<LocalDate, LocalDate> resetStrategy) {
        this.resetStrategy = resetStrategy;
    }

    public LocalDate determineResetDate(LocalDate date) {
        return resetStrategy.apply(date);
    }
}
