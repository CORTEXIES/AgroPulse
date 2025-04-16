package com.github.cortex.exception.bot;

public class TelegramBotException extends RuntimeException {

    public TelegramBotException(String message) {
        super(message);
    }
}
