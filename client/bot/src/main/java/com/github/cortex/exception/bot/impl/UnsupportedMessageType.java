package com.github.cortex.exception.bot.impl;

import com.github.cortex.exception.bot.TelegramBotException;

public class UnsupportedMessageType extends TelegramBotException {
    public UnsupportedMessageType(String message) {
        super(message);
    }
}
