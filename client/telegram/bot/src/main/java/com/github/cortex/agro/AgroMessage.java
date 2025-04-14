package com.github.cortex.agro;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgroMessage {

    private final String senderName;
    private final String telegramId;
    private final String text;

    public AgroMessage(String senderName, String telegramId, String text) {
        this.senderName = senderName;
        this.telegramId = telegramId;
        this.text = text;
    }
}