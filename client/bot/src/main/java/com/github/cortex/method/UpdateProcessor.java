package com.github.cortex.method;

import com.github.cortex.update.UpdateRouter;
import com.github.cortex.telegram.TelegramBot;
import com.github.cortex.telegram.BotRegistrationService;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class UpdateProcessor {

    private final BotRegistrationService registrationService;
    private final UpdateRouter updateRouter;
    private TelegramBot bot;

    @Autowired
    public UpdateProcessor(
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

    public void processUpdates(List<Update> updates) {
        CompletableFuture.runAsync(() -> {
            updates.stream()
                    .map(updateRouter::routeAndHandle)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(method -> bot.sendMessage(method));
        });
    }
}