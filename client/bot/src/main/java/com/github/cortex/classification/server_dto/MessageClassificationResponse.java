package com.github.cortex.classification.server_dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MessageClassificationResponse {
    private List<MessageClassification> response;
}