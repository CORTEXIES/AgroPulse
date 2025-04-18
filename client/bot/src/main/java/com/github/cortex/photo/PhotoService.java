package com.github.cortex.photo;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;

import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;

import java.util.Optional;
import com.github.cortex.agro.service.AgroMessageFactory;

@Component
public class PhotoService {

    private final AgroMessageFactory agroMessageFactory;
    private final PendingPhotoMessageBuffer pendingPhotoMessageBuffer;

    private final String botToken;

    @Autowired
    public PhotoService(
            AgroMessageFactory agroMessageFactory,
            PendingPhotoMessageBuffer pendingPhotoMessageBuffer,
            @Value("${bot.token}") String botToken

    ) {
        this.agroMessageFactory = agroMessageFactory;
        this.pendingPhotoMessageBuffer = pendingPhotoMessageBuffer;
        this.botToken = botToken;
    }

    public Optional<PartialBotApiMethod<?>> requestFileDownload(Message msg) {
        String fileId = msg.hasDocument()
                ? msg.getDocument().getFileId()
                : msg.getPhoto().getFirst().getFileId();

        pendingPhotoMessageBuffer.add(msg);
        return Optional.of(new GetFile(fileId));
    }

    public void recordPhoto(File file) {
        pendingPhotoMessageBuffer.consumeLast()
                .ifPresent(msg -> agroMessageFactory.createAndRecord(
                        msg, Optional.of(file.getFileUrl(botToken)))
                );
    }
}