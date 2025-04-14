package com.github.cortex.classification.database.mapper.impl;

import com.github.cortex.classification.database.dto.MessageClassificationEntity;
import com.github.cortex.classification.database.mapper.EntityMapper;
import com.github.cortex.classification.server_dto.MessageClassification;
import org.springframework.stereotype.Component;

@Component
public class MessageClassificationMapper extends EntityMapper<MessageClassificationEntity, MessageClassification> {

    @Override
    public MessageClassificationEntity toEntity(MessageClassification entityDto) {
        return new MessageClassificationEntity(
                entityDto.getDate(),
                entityDto.getDepartment(),
                entityDto.getOperation(),
                entityDto.getPlant(),
                entityDto.getPerDay(),
                entityDto.getPerOperation(),
                entityDto.getGrosPerDay(),
                entityDto.getGrosPerOperation()
        );
    }

    @Override
    public MessageClassification toDto(MessageClassificationEntity entity) {
        return new MessageClassification(
                entity.getDate(),
                entity.getDepartment(),
                entity.getOperation(),
                entity.getPlant(),
                entity.getPerDay(),
                entity.getPerOperation(),
                entity.getGrosPerDay(),
                entity.getGrosPerOperation()
        );
    }
}