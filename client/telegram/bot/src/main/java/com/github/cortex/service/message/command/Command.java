package com.github.cortex.service.message.command;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

public abstract class Command {

    private final String command;

    public Command(String command) {
        this.command = command;
    }

    public String get() {
        return command;
    }
    
    public abstract BotApiMethod<?> execute();
}