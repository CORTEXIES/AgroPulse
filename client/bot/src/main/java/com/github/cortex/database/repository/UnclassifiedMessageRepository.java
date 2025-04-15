package com.github.cortex.database.repository;

import com.github.cortex.database.dto.agro.UnclassifiedMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnclassifiedMessageRepository extends JpaRepository<UnclassifiedMessageEntity, Long> { }