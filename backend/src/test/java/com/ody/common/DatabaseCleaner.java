package com.ody.common;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.base.CaseFormat;

@Component
public class DatabaseCleaner {

    private static final String FOREIGN_KEY_CHECK_OFF = "SET FOREIGN_KEY_CHECKS = 0";
    private static final String FOREIGN_KEY_CHECK_ON = "SET FOREIGN_KEY_CHECKS = 1";
    private static final String TRUNCATE_TABLE_FORMAT = "TRUNCATE TABLE %s";
    private static final String ALTER_TABLE_AUTO_INCREMENT_FORMAT = "ALTER TABLE %s AUTO_INCREMENT = %d";

    private List<String> tableNames = new ArrayList<>();

    @PersistenceContext
    private EntityManager entityManager;

    public DatabaseCleaner(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @PostConstruct
    void findTablesNames() {
        this.tableNames = entityManager.getMetamodel()
                .getEntities().stream()
                .filter(entityType -> entityType.getJavaType().getAnnotation(Entity.class) != null)
                .map(entityType -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityType.getName()))
                .toList();
    }

    @Transactional
    public void cleanUp() {
        entityManager.createNativeQuery(FOREIGN_KEY_CHECK_OFF).executeUpdate();

        for (String tableName : tableNames) {
            entityManager.createNativeQuery(String.format(TRUNCATE_TABLE_FORMAT, tableName)).executeUpdate();
            entityManager.createNativeQuery(String.format(ALTER_TABLE_AUTO_INCREMENT_FORMAT, tableName, 1))
                    .executeUpdate();
        }
        entityManager.createNativeQuery(FOREIGN_KEY_CHECK_ON).executeUpdate();

        entityManager.flush();
        entityManager.clear();
    }
}
