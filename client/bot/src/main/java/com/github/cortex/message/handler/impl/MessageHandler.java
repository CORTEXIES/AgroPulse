package com.github.cortex.message.handler.impl;

import com.github.cortex.agro.service.AgroMessageFactory;
import com.github.cortex.message.handler.Handler;
import com.github.cortex.command.utils.CommandExecutor;

import com.github.cortex.photo.PhotoService;
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

    private final CommandExecutor commandExecutor;
    private final AgroMessageFactory agroMessageFactory;
    private final PhotoService photoService;

    @Value("${bot.command.prefix}")
    private String COMMAND_PREFIX;

    @Autowired
    public MessageHandler(
            CommandExecutor commandExecutor,
            AgroMessageFactory agroMessageFactory,
            PhotoService photoService
    ) {
        this.agroMessageFactory = agroMessageFactory;
        this.commandExecutor = commandExecutor;
        this.photoService = photoService;
    }

    @Override
    public boolean isApplicable(Update update) {
        return update.hasMessage();
    }

    @Override
    public Optional<PartialBotApiMethod<?>> handle(Update update) {
        Message msg = update.getMessage();

        if (!hasRelevantContent(msg)) return Optional.empty();

        if (isCommand(msg)) {
            return commandExecutor.execute(msg);
        }

        if (isLongText(msg)) {
            agroMessageFactory.createAndRecord(msg, Optional.empty());
            return Optional.empty();
        }

        return photoService.requestFileDownload(msg);
    }

    private boolean isCommand(Message msg) {
        return msg.hasText() && msg.getText().startsWith(COMMAND_PREFIX);
    }

    private boolean isLongText(Message msg) {
        return msg.hasText() && msg.getText().length() >= MINIMAL_TEXT_LENGTH_FOR_RECORD;
    }

    private boolean hasRelevantContent(Message msg) {
        return msg.hasText() || msg.hasPhoto() || isSupportedImageDocument(msg);
    }

    private boolean isSupportedImageDocument(Message msg) {
        return msg.hasDocument() &&
                msg.getDocument().getMimeType() != null &&
                msg.getDocument().getMimeType().equals("image/jpeg") ||
                msg.getDocument().getMimeType().equals("image/png");
    }
}