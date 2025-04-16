package com.github.cortex.command;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;

public abstract class Command {

    private final String command;

    public Command(String command) {
        this.command = command;
    }

    public String get() {
        return command;
    }
    
    public abstract PartialBotApiMethod<?> execute(Message msg);
}