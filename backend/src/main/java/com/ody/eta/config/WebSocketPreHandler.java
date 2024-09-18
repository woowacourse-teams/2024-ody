package com.ody.eta.config;

import com.ody.common.exception.OdyWebSocketException;
import com.ody.eta.domain.WebSocketEndpoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

@Configuration
public class WebSocketPreHandler implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        String destination = accessor.getDestination();

        if (StompCommand.SUBSCRIBE.equals(command)) {
            validateSubscribeEndpoint(destination);
        }
        if (StompCommand.SEND.equals(command)) {
            validateSendEndpoint(destination);
        }
        return message;
    }

    private void validateSubscribeEndpoint(String destination) {
        if (WebSocketEndpoint.getSubscribeEndpoints().contains(destination)) {
            return;
        }
        throw new OdyWebSocketException(destination + "은 유효하지 않은 subscribe endpoint 입니다.");
    }

    private void validateSendEndpoint(String destination) {
        if (WebSocketEndpoint.getSendEndpoints().contains(destination)) {
            return;
        }
        throw new OdyWebSocketException(destination + "은 유효하지 않은 send endpoint 입니다.");
    }
}
