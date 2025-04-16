package com.github.cortex.message.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;

import java.util.Optional;

public interface Handler {
    boolean isApplicable(Update update);
    Optional<PartialBotApiMethod<?>> handle(Update update);
}