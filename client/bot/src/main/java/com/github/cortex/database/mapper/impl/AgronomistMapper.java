package com.github.cortex.database.mapper.impl;

import com.github.cortex.agro.dto.Agronomist;
import com.github.cortex.database.dto.agro.AgronomistEntity;
import com.github.cortex.database.mapper.EntityMapper;
import org.springframework.stereotype.Component;

@Component
public class AgronomistMapper extends EntityMapper<AgronomistEntity, Agronomist> {

    @Override
    public AgronomistEntity toEntity(Agronomist entityDto) {
        return new AgronomistEntity(
                entityDto.fullName(),
                entityDto.telegramId()
        );
    }

    @Override
    public Agronomist toDto(AgronomistEntity entity) {
        return new Agronomist(
                entity.getFullName(),
                entity.getTelegramId()
        );
    }
}