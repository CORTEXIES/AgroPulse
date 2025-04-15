package com.github.cortex.telegram;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;

import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import jakarta.annotation.PostConstruct;
import com.github.cortex.exception.bot.impl.UnsupportedMessageType;

@Log4j2
@Component
public class TelegramBot implements LongPollingUpdateConsumer {

    private final TelegramClient client;
    private final TelegramBotController controller;

    @Autowired
    public TelegramBot(
            @Value("${bot.token}") String botToken,
            TelegramBotController controller
    ) {
        this.client = new OkHttpTelegramClient(botToken);
        this.controller = controller;
    }

    @PostConstruct
    public void init() {
        controller.registerBot(this);
    }

    @Override
    public void consume(List<Update> updates) {
        controller.handleUpdates(updates);
    }

    public void sendMessage(PartialBotApiMethod<?> answerMessage) {
        try {
            switch (answerMessage) {
                case SendMessage sendMessage -> client.execute(sendMessage);
                case SendDocument sendDocument -> client.execute(sendDocument);
                default -> throw createUnsupportedMessageTypeException(answerMessage);
            }
        } catch (TelegramApiException ex) {
            log.error("Error when sending a answer message", ex);
        }
    }

    private UnsupportedMessageType createUnsupportedMessageTypeException(PartialBotApiMethod<?> answerMessage) {
        String type = answerMessage.getClass().getSimpleName();
        log.info("Unsupported message type: {}", type);
        return new UnsupportedMessageType("Type: " + type);
    }
}