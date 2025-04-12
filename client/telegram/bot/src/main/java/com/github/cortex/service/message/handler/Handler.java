package com.github.cortex.service.message.handler;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public interface Handler{
    boolean isApplicable(Update update);
    Optional<BotApiMethod<?>> handle(Update update);
}