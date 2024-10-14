package com.ody.common;

import com.ody.common.config.JpaAuditingConfig;
import com.ody.eta.repository.EtaRepository;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.repository.MemberRepository;
import com.ody.notification.repository.NotificationRepository;
import com.ody.route.repository.ApiCallRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import({JpaAuditingConfig.class, FixtureGeneratorConfig.class, TestAuthConfig.class, MySQLTestContainersConfig.class, DatabaseCleaner.class})
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
public abstract class BaseRepositoryTest {

    @Autowired
    protected FixtureGenerator fixtureGenerator;

    @Autowired
    protected MeetingRepository meetingRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected MateRepository mateRepository;

    @Autowired
    protected NotificationRepository notificationRepository;

    @Autowired
    protected EtaRepository etaRepository;

    @Autowired
    protected ApiCallRepository apiCallRepository;

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void cleanUp() {
        databaseCleaner.cleanUp();
    }
}
