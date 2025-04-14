package com.github.cortex.classification.utils;

import com.github.cortex.classification.ClassifiedMessagePair;
import com.github.cortex.classification.database.MessageClassificationRepository;
import com.github.cortex.classification.database.dto.MessageClassificationEntity;
import com.github.cortex.classification.database.dto.Status;
import com.github.cortex.classification.database.mapper.EntityMapper;
import com.github.cortex.classification.server_dto.MessageClassification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageClassificationFetcher {

    private final MessageClassificationRepository messageClassificationRepository;
    private final EntityMapper<MessageClassificationEntity, MessageClassification> messageClassificationEntityMapper;

    public MessageClassificationFetcher (
        MessageClassificationRepository messageClassificationRepository,
        EntityMapper<MessageClassificationEntity, MessageClassification> messageClassificationEntityMapper
    ) {
        this.messageClassificationRepository = messageClassificationRepository;
        this.messageClassificationEntityMapper = messageClassificationEntityMapper;
    }

    public List<ClassifiedMessagePair> fetchNewMessages() {
        return messageClassificationRepository.findByStatus(Status.NEW).stream()
                .map(entity -> new ClassifiedMessagePair(
                        entity, messageClassificationEntityMapper.toDto(entity)
                ))
                .toList();
    }
}