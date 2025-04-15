package com.github.cortex.exception.bot.impl;

import com.github.cortex.exception.bot.TelegramBotException;

public class RegistrationException extends TelegramBotException {

    public RegistrationException(String message) {
        super(message);
    }
}
