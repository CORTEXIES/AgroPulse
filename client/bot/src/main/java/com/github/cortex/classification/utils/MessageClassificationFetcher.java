package com.github.cortex.classification.utils;

import com.github.cortex.database.mapper.EntityMapper;
import com.github.cortex.database.dto.classifcation.ClassificationStatus;
import com.github.cortex.database.dto.classifcation.MessageClassificationEntity;
import com.github.cortex.database.repository.MessageClassificationRepository;

import com.github.cortex.classification.ClassifiedMessagePair;
import com.github.cortex.classification.dto.MessageClassification;

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
        return messageClassificationRepository.findByClassificationStatus(ClassificationStatus.NEW).stream()
                .map(entity -> new ClassifiedMessagePair(
                        entity, messageClassificationEntityMapper.toDto(entity)
                ))
                .toList();
    }
}