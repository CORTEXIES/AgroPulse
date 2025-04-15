package com.github.cortex.classification.service;

import com.github.cortex.agro.AgroMessage;
import com.github.cortex.agro.AgroMessageUtils;
import com.github.cortex.classification.database.mapper.EntityMapper;
import com.github.cortex.classification.database.dto.MessageClassificationEntity;
import com.github.cortex.classification.database.MessageClassificationRepository;

import com.github.cortex.classification.server_dto.MessageClassification;
import com.github.cortex.exception.classifiaction.EmptyClassifiedMessagesException;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class MessageClassificationPersistService {

    private final MessageClassificationExchangeClient exchangeClient;
    private final MessageClassificationRepository messageClassificationRepository;
    private final EntityMapper<MessageClassificationEntity, MessageClassification> messageClassificationEntityMapper;

    @Autowired
    public MessageClassificationPersistService(
            MessageClassificationRepository messageClassificationRepository,
            EntityMapper<MessageClassificationEntity, MessageClassification> messageClassificationEntityMapper,
            MessageClassificationExchangeClient exchangeClient
    ) {
        this.messageClassificationRepository = messageClassificationRepository;
        this.messageClassificationEntityMapper = messageClassificationEntityMapper;
        this.exchangeClient = exchangeClient;
    }

    public void execute(List<AgroMessage> messages) {
        List<MessageClassification> classifiedMessages = exchangeClient.executeClassification(messages);
        if (classifiedMessages.isEmpty()) {
            throw createEmptyClassifiedMsgException(messages);
        }
        List<MessageClassificationEntity> entities = classifiedMessages.stream()
                .map(messageClassificationEntityMapper::toEntity)
                .toList();
        messageClassificationRepository.saveAll(entities);
    }

    private EmptyClassifiedMessagesException createEmptyClassifiedMsgException(List<AgroMessage> unclassifiedMessages) {
        String data = AgroMessageUtils.extractFieldData(unclassifiedMessages);
        log.error("The server returned an empty list of classified messages! Unclassified messages: {}", data);
        return new EmptyClassifiedMessagesException("Unclassified messages: " + data);
    }
}