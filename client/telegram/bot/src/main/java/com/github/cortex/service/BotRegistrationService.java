package com.github.cortex.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;

import com.github.cortex.exception.bot.impl.RegistrationException;

@Log4j2
@Service
public class BotRegistrationService {

    @Value("${bot.token}")
    private String BOT_TOKEN;

    public void register(LongPollingUpdateConsumer bot) {
        try {
            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(BOT_TOKEN, bot);
            log.info("Successful registration of the bot. Bot token: {}", BOT_TOKEN);
        } catch (TelegramApiException ex) { throw createRegistrationException(); }
    }

    private RegistrationException createRegistrationException() {
        log.error("Bot registration error by token: {}", BOT_TOKEN);
        throw new RegistrationException("Bot token: " + BOT_TOKEN);
    }
}