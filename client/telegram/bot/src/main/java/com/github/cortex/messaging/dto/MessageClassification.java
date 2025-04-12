package com.github.cortex.messaging.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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