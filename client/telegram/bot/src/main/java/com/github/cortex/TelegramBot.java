package com.github.cortex;

import com.github.cortex.controll.TelegramBotController;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import jakarta.annotation.PostConstruct;

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

    public void sendMessage(BotApiMethod<?> answerMessage) {
        try {
            client.execute(answerMessage);
            log.info("The answer message was sent successfully");
        } catch (TelegramApiException ex) {
            log.error("Error when sending a answer message", ex);
        }
    }
}