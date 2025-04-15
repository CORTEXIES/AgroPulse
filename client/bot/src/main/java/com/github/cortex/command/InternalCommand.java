package com.github.cortex.command;

import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public abstract class InternalCommand {
    public abstract PartialBotApiMethod<?> execute(Message msg);
}