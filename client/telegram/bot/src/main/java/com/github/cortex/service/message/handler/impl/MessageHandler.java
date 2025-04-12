package com.github.cortex.service.message.handler.impl;

import com.github.cortex.service.message.UserMessageService;
import com.github.cortex.service.message.command.CommandExecutor;
import com.github.cortex.service.message.handler.Handler;
import com.github.cortex.service.message.photo.PhotoController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.Optional;

@Service
public class MessageHandler implements Handler {

    private final PhotoController photoController;
    private final UserMessageService userMessageService;
    private final CommandExecutor commandExecutor;

    @Value("${bot.command.prefix}")
    private String COMMAND_PREFIX;

    @Autowired
    public MessageHandler(
            PhotoController photoController,
            UserMessageService userMessageService,
            CommandExecutor commandExecutor
    ) {
        this.photoController = photoController;
        this.userMessageService = userMessageService;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public boolean isApplicable(Update update) {
        return update.hasMessage();
    }

    @Override
    public Optional<BotApiMethod<?>> handle(Update update) {
        Message msg = update.getMessage();
        if (msg.hasText()) {
            return handleText(msg);
        } else if (msg.hasPhoto()) {
            photoController.handle(msg.getPhoto());
        }
        return Optional.empty();
    }

    private Optional<BotApiMethod<?>> handleText(Message msg) {
        String text = msg.getText();
        if (text.startsWith(COMMAND_PREFIX)) {
            return commandExecutor.execute(text);
        }
        userMessageService.extractAndRecord(msg);
        return Optional.empty();
    }
}