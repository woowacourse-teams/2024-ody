package com.ody.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class ReplicationDataSourceRouter extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        ReplicationType type = ReplicationType.from(isTransactionActive, readOnly);
        log.info("(트랜잭션 활성화 여부 : {}) (readOnly : {}) => {} DB 연결", isTransactionActive, isTransactionActive, type);
        return type;
    }
}
