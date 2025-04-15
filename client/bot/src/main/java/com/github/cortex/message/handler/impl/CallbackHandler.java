package com.github.cortex.message.handler.impl;

import org.springframework.stereotype.Service;
import com.github.cortex.message.handler.Handler;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;

import java.util.Optional;

@Service
public class CallbackHandler implements Handler {

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery();
    }

    @Override
    public Optional<PartialBotApiMethod<?>> handle(Update data) {
        return null; //TODO: заглушка
    }
}