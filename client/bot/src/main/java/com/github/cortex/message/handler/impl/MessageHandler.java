package com.github.cortex.message.handler.impl;

import com.github.cortex.photo.PhotoController;
import com.github.cortex.agro.service.AgroMessageFactory;
import com.github.cortex.message.handler.Handler;
import com.github.cortex.command.utils.CommandExecutor;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;

import java.util.Optional;

@Service
public class MessageHandler implements Handler {

    private final static int MINIMAL_TEXT_LENGTH_FOR_RECORD = 30;

    private final PhotoController photoController;
    private final AgroMessageFactory agroMessageFactory;
    private final CommandExecutor commandExecutor;

    @Value("${bot.command.prefix}")
    private String COMMAND_PREFIX;

    @Autowired
    public MessageHandler(
            PhotoController photoController,
            AgroMessageFactory agroMessageFactory,
            CommandExecutor commandExecutor
    ) {
        this.photoController = photoController;
        this.agroMessageFactory = agroMessageFactory;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public boolean isApplicable(Update update) {
        return update.hasMessage();
    }

    @Override
    public Optional<PartialBotApiMethod<?>> handle(Update update) {
        Message msg = update.getMessage();
        if (msg.hasText()) {
            return handleText(msg);
        } else if (msg.hasPhoto()) {
            photoController.handle(msg.getPhoto());
        }
        return Optional.empty();
    }

    private Optional<PartialBotApiMethod<?>> handleText(Message msg) {
        String text = msg.getText();
        if (text.startsWith(COMMAND_PREFIX)) {
            return commandExecutor.execute(msg);
        } else if (text.length() >= MINIMAL_TEXT_LENGTH_FOR_RECORD) {
			agroMessageFactory.createAndRecord(msg);
        }
        return Optional.empty();
    }
}