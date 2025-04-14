package com.github.cortex.update;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;

import com.github.cortex.message.handler.Handler;
import com.github.cortex.exception.bot.impl.HandlerNotFoundException;

import java.util.List;
import java.util.Optional;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class UpdateRouter {

    private final List<Handler> handlers;

    @Autowired
    public UpdateRouter(List<Handler> handlers) {
        this.handlers = handlers;
    }

    public Optional<PartialBotApiMethod<?>> routeAndHandle(Update update) {
        return handlers.stream()
                .filter(h -> h.isApplicable(update))
                .findFirst()
                .map(h -> h.handle(update))
                .orElseThrow(() -> createHandlerNotFoundException(update));
    }

    private HandlerNotFoundException createHandlerNotFoundException(Update update) {
        String errMsg = "The handler for this entity was not found. Entity: ";
        log.error(errMsg, update.toString());
        return new HandlerNotFoundException(errMsg + update);
    }
}