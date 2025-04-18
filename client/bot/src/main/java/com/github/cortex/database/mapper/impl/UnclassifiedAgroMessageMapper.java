package com.github.cortex.database.mapper.impl;

import com.github.cortex.agro.dto.AgroMessage;
import com.github.cortex.agro.dto.Agronomist;
import com.github.cortex.database.dto.agro.AgronomistEntity;
import com.github.cortex.database.dto.classifcation.UnclassifiedMessageEntity;
import com.github.cortex.database.dto.classifcation.UnclassifiedMessageStatus;
import com.github.cortex.database.mapper.EntityMapper;
import com.github.cortex.database.repository.AgronomistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

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
                agroMsg.report().get(),
                LocalDateTime.now(),
                UnclassifiedMessageStatus.UNCLASSIFIED
        );
    }

    @Override
    public AgroMessage toDto(UnclassifiedMessageEntity entity) {
        return new AgroMessage(
                agronomistMapper.toDto(entity.getAgronomist()),
                Optional.of(entity.getReport()),
                Optional.empty() //TODO: EDIT
        );
    }
}
