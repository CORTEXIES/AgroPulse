package com.github.cortex.classification;

import com.github.cortex.classification.server_dto.MessageClassification;
import com.github.cortex.classification.database.dto.MessageClassificationEntity;


public record ClassifiedMessagePair (
    MessageClassificationEntity entity,
    MessageClassification dto
) {}