package com.github.cortex.controll;

import com.github.cortex.TelegramBot;
import com.github.cortex.service.BotRegistrationService;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TelegramBotController {

    private final BotRegistrationService registrationService;
    private final UpdateRouter updateRouter;
    private TelegramBot bot;

    @Autowired
    public TelegramBotController(
            BotRegistrationService registrationService,
            UpdateRouter updateRouter
    ) {
        this.registrationService = registrationService;
        this.updateRouter = updateRouter;
    }

	public void registerBot(TelegramBot bot) {
        this.bot = bot;
        registrationService.register(bot);
    }

    public void handleUpdates(List<Update> updates) {
        CompletableFuture.runAsync(() -> {
            updates.stream()
                    .map(u -> updateRouter.routeAndHandle(u))
                    .filter(optional -> optional.isPresent())
                    .map(optional -> optional.get())
                    .forEach(method -> bot.sendMessage(method));
        });
    }
}