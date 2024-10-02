package com.ody.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleaner {

    @PersistenceContext
    private EntityManager entityManager;

    public DatabaseCleaner(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void cleanUp() {
        cleanNotification();
        cleanEta();
        cleanMate();
        cleanMeeting();
        cleanMember();
    }

    private void cleanNotification() {
        entityManager.createNativeQuery("DELETE FROM Notification")
                .executeUpdate();

        entityManager.createNativeQuery("ALTER TABLE notification ALTER COLUMN id RESTART WITH 1")
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();
    }

    private void cleanEta() {
        entityManager.createNativeQuery("DELETE FROM Eta")
                .executeUpdate();

        entityManager.createNativeQuery("ALTER TABLE eta ALTER COLUMN id RESTART WITH 1")
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();
    }


    private void cleanMate() {
        entityManager.createNativeQuery("DELETE FROM Mate")
                .executeUpdate();

        entityManager.createNativeQuery("ALTER TABLE mate ALTER COLUMN id RESTART WITH 1")
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();
    }

    private void cleanMeeting() {
        entityManager.createNativeQuery("DELETE FROM Meeting")
                .executeUpdate();

        entityManager.createNativeQuery("ALTER TABLE meeting ALTER COLUMN id RESTART WITH 1")
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();
    }

    private void cleanMember() {
        entityManager.createNativeQuery("DELETE FROM Member")
                .executeUpdate();

        entityManager.createNativeQuery("ALTER TABLE member ALTER COLUMN id RESTART WITH 1")
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();
    }
}
