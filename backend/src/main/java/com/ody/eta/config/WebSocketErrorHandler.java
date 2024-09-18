package com.ody.eta.config;

import com.ody.common.exception.OdyException;
import com.ody.common.exception.OdyWebSocketException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Slf4j
@Configuration
public class WebSocketErrorHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        Throwable cause = ex.getCause();
        String message = cause.getMessage();

        if (cause instanceof OdyWebSocketException) {
            log.warn("message: {}", message);
            return createErrorMessage(message);
        }

        if (cause instanceof OdyException) {
            log.warn("message: {}", message);
            return createErrorMessage(message);
        }

        if (cause instanceof Exception) {
            log.error("exception: {}", cause);
            return createErrorMessage("서버 에러");
        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Message<byte[]> createErrorMessage(String exceptionMessage) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        return MessageBuilder.createMessage(exceptionMessage.getBytes(), accessor.getMessageHeaders());
    }
}
