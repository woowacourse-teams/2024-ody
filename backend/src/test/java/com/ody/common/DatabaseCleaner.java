package com.ody.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleaner {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void cleanUp() {
        cleanNotification();
        cleanMate();
        cleanMeeting();
        cleanMember();
    }

    private void cleanNotification() {
        entityManager.createQuery("DELETE FROM Notification")
                .executeUpdate();

        entityManager.createNativeQuery("ALTER TABLE notification ALTER COLUMN id RESTART WITH 1")
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();
    }

    private void cleanMate() {
        entityManager.createQuery("DELETE FROM Mate")
                .executeUpdate();

        entityManager.createNativeQuery("ALTER TABLE mate ALTER COLUMN id RESTART WITH 1")
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();
    }

    private void cleanMeeting() {
        entityManager.createQuery("DELETE FROM Meeting")
                .executeUpdate();

        entityManager.createNativeQuery("ALTER TABLE meeting ALTER COLUMN id RESTART WITH 1")
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();
    }

    private void cleanMember() {
        entityManager.createQuery("DELETE FROM Member")
                .executeUpdate();

        entityManager.createNativeQuery("ALTER TABLE member ALTER COLUMN id RESTART WITH 1")
                .executeUpdate();

        entityManager.flush();
        entityManager.clear();
    }
}
