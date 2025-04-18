package com.github.cortex.database.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;

import com.github.cortex.database.dto.classifcation.ClassificationStatus;
import com.github.cortex.database.dto.classifcation.MessageClassificationEntity;

import java.util.List;
import jakarta.transaction.Transactional;

public interface MessageClassificationRepository extends JpaRepository<MessageClassificationEntity, Long> {

    List<MessageClassificationEntity> findByClassificationStatus(ClassificationStatus classificationStatus);

    @Modifying
    @Transactional
    @Query("UPDATE MessageClassificationEntity m SET m.classificationStatus = :status WHERE m.id IN :ids")
    void markAsProcessed(@Param("status") ClassificationStatus classificationStatus, @Param("ids") List<Long> ids);
}