package com.github.cortex.exception;

public class HandlerNotFoundException extends TelegramBotException {
    public HandlerNotFoundException(String message) {
        super(message);
    }
}
