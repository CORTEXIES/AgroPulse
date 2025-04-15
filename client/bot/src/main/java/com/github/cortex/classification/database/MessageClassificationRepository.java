package com.github.cortex.classification.database;

import com.github.cortex.classification.database.dto.MessageClassificationEntity;
import com.github.cortex.classification.database.dto.Status;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageClassificationRepository extends JpaRepository<MessageClassificationEntity, Long> {

    List<MessageClassificationEntity> findByStatus(Status status);

    @Modifying
    @Transactional
    @Query("UPDATE MessageClassificationEntity m SET m.status = :status WHERE m.id IN :ids")
    void markAsProcessed(@Param("status") Status status, @Param("ids") List<Long> ids);
}