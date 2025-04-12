package com.github.cortex.messaging.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMessage {

    private final String senderName;
    private final String telegramId;
    private final String text;

    public UserMessage(String senderName, String telegramId, String text) {
        this.senderName = senderName;
        this.telegramId = telegramId;
        this.text = text;
    }
}
