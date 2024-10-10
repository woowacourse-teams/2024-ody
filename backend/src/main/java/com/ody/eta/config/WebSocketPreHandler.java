package com.ody.eta.config;

import com.ody.common.exception.OdyWebSocketException;
import com.ody.eta.domain.WebSocketEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

@Slf4j
@Configuration
public class WebSocketPreHandler implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        String destination = accessor.getDestination();

        if (command == null) {
            throw new OdyWebSocketException("stomp command는 null 일 수 없습니다.");
        }
        if (StompCommand.SUBSCRIBE.equals(command)) {
            validateSubEndpoint(destination);
        }
        if (StompCommand.SEND.equals(command)) {
            validateSendEndpoint(destination);
        }
        return message;
    }

    private void validateSubEndpoint(String destination) {
        if (invalidSubEndpoint(destination)) {
            throw new OdyWebSocketException(destination + "은 유효하지 않은 subscribe endpoint 입니다.");
        }
    }

    private boolean invalidSubEndpoint(String destination) {
        return destination == null || WebSocketEndpoint.getSubscribeEndpoints().stream()
                .noneMatch(destination::startsWith);
    }

    private void validateSendEndpoint(String destination) {
        if (invalidSendEndpoint(destination)) {
            throw new OdyWebSocketException(destination + "은 유효하지 않은 send endpoint 입니다.");
        }
    }

    private boolean invalidSendEndpoint(String destination) {
        return destination == null || WebSocketEndpoint.getSendEndpoints().stream()
                .noneMatch(destination::startsWith);
    }
}
