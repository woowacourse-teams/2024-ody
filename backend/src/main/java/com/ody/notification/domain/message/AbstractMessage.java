package com.ody.notification.domain.message;

import com.google.firebase.messaging.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class AbstractMessage {

    private final Message message;
}
