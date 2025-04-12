package com.github.cortex.controll;

import com.github.cortex.exception.HandlerNotFoundException;
import com.github.cortex.service.message.handler.Handler;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UpdateRouter {

    private final List<Handler> handlers;

    @Autowired
    public UpdateRouter(List<Handler> handlers) {
        this.handlers = handlers;
    }

    public Optional<BotApiMethod<?>> routeAndHandle(Update update) {
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