package com.github.cortex.classification.utils;

import com.github.cortex.database.dto.classifcation.ClassificationStatus;
import com.github.cortex.database.repository.MessageClassificationRepository;
import com.github.cortex.database.dto.classifcation.MessageClassificationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        messageClassificationRepository.markAsProcessed(ClassificationStatus.PROCESSED, ids);
    }
}