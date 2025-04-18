package com.github.cortex.agro.service;

import com.github.cortex.database.dto.classifcation.UnclassifiedMessageEntity;
import com.github.cortex.database.mapper.impl.UnclassifiedAgroMessageMapper;
import com.github.cortex.database.repository.UnclassifiedMessageRepository;
import com.github.cortex.agro.dto.AgroMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnclassifiedMessageService {

    private final UnclassifiedMessageRepository repository;
    private final UnclassifiedAgroMessageMapper unclassifiedAgroMessageMapper;

    @Autowired
    public UnclassifiedMessageService(
            UnclassifiedMessageRepository repository,
            UnclassifiedAgroMessageMapper unclassifiedAgroMessageMapper
    ) {
        this.repository = repository;
        this.unclassifiedAgroMessageMapper = unclassifiedAgroMessageMapper;
    }

    public void record(List<AgroMessage> messages) {
        List<UnclassifiedMessageEntity> entities = messages.stream()
                .map(unclassifiedAgroMessageMapper::toEntity)
                .toList();
        repository.saveAll(entities);
    }
}