package com.github.cortex.database.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.github.cortex.database.dto.classifcation.UnclassifiedMessageEntity;

@Repository
public interface UnclassifiedMessageRepository extends JpaRepository<UnclassifiedMessageEntity, Long> { }