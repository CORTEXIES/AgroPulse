package com.github.cortex.classification.server_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MessageClassification {
    private LocalDateTime date;

    private String department;
    private String operation;
    private String plant;

    private int perDay;
    private int perOperation;
    private int grosPerDay;
    private int grosPerOperation;
}