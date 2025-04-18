package com.github.cortex.command;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;

public abstract class InternalCommand {
    public abstract PartialBotApiMethod<?> execute(Message msg);
}