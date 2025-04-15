package com.github.cortex.database.repository;

import com.github.cortex.database.dto.agro.AgronomistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgronomistRepository extends JpaRepository<AgronomistEntity, Long> {
    Optional<AgronomistEntity> findByTelegramId(String telegramId);
}