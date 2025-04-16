package com.github.cortex.exception.bot.impl;

import com.github.cortex.exception.bot.TelegramBotException;

public class HandlerNotFoundException extends TelegramBotException {
    public HandlerNotFoundException(String message) {
        super(message);
    }
}
