package com.ody.common.websocket;

import com.ody.common.TestAuthConfig;
import com.ody.common.TestRouteConfig;
import com.ody.common.config.JpaAuditingConfig;
import com.ody.eta.config.WebSocketConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@ExtendWith(SpringExtension.class)
@Import({JpaAuditingConfig.class, TestRouteConfig.class, TestAuthConfig.class, WebSocketConfig.class})
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseStompTest {

    private static final String ENDPOINT ="/connect";

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
                .connect(url + port + ENDPOINT, new StompSessionHandlerAdapter() {})
                .get(3, TimeUnit.SECONDS);
        System.out.println("연결됨");
    }

    @AfterEach
    public void disconnect() {
        if (this.stompSession.isConnected()) {
            this.stompSession.disconnect();
        }
    }

    private List<Transport> createTransport() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }
}
