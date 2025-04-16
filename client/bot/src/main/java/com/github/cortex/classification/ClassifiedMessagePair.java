package com.github.cortex.classification;

import com.github.cortex.classification.dto.MessageClassification;
import com.github.cortex.database.dto.classifcation.MessageClassificationEntity;


public record ClassifiedMessagePair (
    MessageClassificationEntity entity,
    MessageClassification dto
) {}