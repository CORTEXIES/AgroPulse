package com.github.cortex.service.message.handler.impl;

import com.github.cortex.service.message.handler.Handler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Service
public class CallbackHandler implements Handler {

    @Override
    public boolean isApplicable(Update update) {
        return update.hasCallbackQuery();
    }

    @Override
    public Optional<BotApiMethod<?>> handle(Update data) {
        return null; //TODO: заглушка
    }
}