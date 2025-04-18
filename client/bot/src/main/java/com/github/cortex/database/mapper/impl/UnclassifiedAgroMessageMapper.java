package com.github.cortex.database.mapper.impl;

import com.github.cortex.agro.dto.Agronomist;
import com.github.cortex.agro.dto.AgroMessage;
import com.github.cortex.database.mapper.EntityMapper;
import com.github.cortex.database.dto.agro.AgronomistEntity;
import com.github.cortex.database.repository.AgronomistRepository;
import com.github.cortex.database.dto.classifcation.UnclassifiedMessageEntity;
import com.github.cortex.database.dto.classifcation.UnclassifiedMessageStatus;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.time.LocalDateTime;

@Component
public class UnclassifiedAgroMessageMapper extends EntityMapper<UnclassifiedMessageEntity, AgroMessage> {

    private final AgronomistMapper agronomistMapper;
    private final AgronomistRepository agronomistRepository;

    @Autowired
    public UnclassifiedAgroMessageMapper(
            AgronomistMapper agronomistMapper,
            AgronomistRepository agronomistRepository
    ) {
        this.agronomistMapper = agronomistMapper;
        this.agronomistRepository = agronomistRepository;
    }

    @Override
    public UnclassifiedMessageEntity toEntity(AgroMessage agroMsg) {

        Agronomist agronomistDto = agroMsg.agronomist();
        AgronomistEntity entity = agronomistRepository
                .findByTelegramId(agronomistDto.telegramId())
                .orElseGet(() -> {
                    AgronomistEntity newEntity = new AgronomistEntity(
                            agronomistDto.fullName(),
                            agronomistDto.telegramId()
                    );
                    return agronomistRepository.save(newEntity);
                });

        return new UnclassifiedMessageEntity(
                entity,
                agroMsg.report().orElse(null),
                agroMsg.photoUrl().orElse(null),
                LocalDateTime.now(),
                UnclassifiedMessageStatus.UNCLASSIFIED
        );
    }

    @Override
    public AgroMessage toDto(UnclassifiedMessageEntity entity) {
        return new AgroMessage(
                agronomistMapper.toDto(entity.getAgronomist()),
                Optional.of(entity.getReport()),
                Optional.of(entity.getPhotoUrl())
        );
    }
}