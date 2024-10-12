package com.ody.common.websocket;

import com.ody.common.DatabaseCleaner;
import com.ody.common.TestAuthConfig;
import com.ody.common.TestRouteConfig;
import com.ody.common.config.JpaAuditingConfig;
import com.ody.eta.config.WebSocketConfig;
import com.ody.notification.config.FcmConfig;
import com.ody.notification.service.FcmPushSender;
import com.ody.notification.service.FcmSubscriber;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Import({JpaAuditingConfig.class, TestRouteConfig.class, TestAuthConfig.class, WebSocketConfig.class})
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class BaseStompTest {

    private static final String ENDPOINT = "/connect";

    @MockBean
    private FcmConfig fcmConfig;

    @MockBean
    protected FcmSubscriber fcmSubscriber;

    @MockBean
    protected FcmPushSender fcmPushSender;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    protected StompSession stompSession;

    @LocalServerPort
    private int port;

    private final String url;

    private final WebSocketStompClient websocketClient;

    public BaseStompTest() {
        this.websocketClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.websocketClient.setMessageConverter(new MappingJackson2MessageConverter());
        this.url = "ws://localhost:";
    }

    @BeforeEach
    public void connect() throws ExecutionException, InterruptedException, TimeoutException {
        this.stompSession = this.websocketClient
                .connect(url + port + ENDPOINT, new StompSessionHandlerAdapter() {
                })
                .get(3, TimeUnit.SECONDS);
    }

    @BeforeEach
    void databaseCleanUp() {
        databaseCleaner.cleanUp();
    }

    @AfterEach
    public void disconnect() {
        if (this.stompSession.isConnected()) {
            this.stompSession.disconnect();
        }
    }
}
