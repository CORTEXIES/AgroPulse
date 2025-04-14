package com.github.cortex.classification.utils;

import com.github.cortex.classification.database.MessageClassificationRepository;
import com.github.cortex.classification.database.dto.MessageClassificationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.cortex.classification.database.dto.Status;

import java.util.List;

@Component
public class MessageClassificationStatusUpdater {

    private final MessageClassificationRepository messageClassificationRepository;

    @Autowired
    public MessageClassificationStatusUpdater(MessageClassificationRepository messageClassificationRepository) {
        this.messageClassificationRepository = messageClassificationRepository;
    }

    public void markMessagesAsProcessed(List<MessageClassificationEntity> entities) {
        List<Long> ids = entities.stream()
                .map(MessageClassificationEntity::getId)
                .toList();
        messageClassificationRepository.markAsProcessed(Status.PROCESSED, ids);
    }
}