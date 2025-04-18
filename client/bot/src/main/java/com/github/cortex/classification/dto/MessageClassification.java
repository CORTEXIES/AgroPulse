package com.github.cortex.classification.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageClassification {

    private LocalDateTime date;

    @JsonProperty("department")
    private String department;

    @JsonProperty("operation")
    private String operation;

    @JsonProperty("plant")
    private String plant;

    @JsonProperty("perDay")
    private int perDay;

    @JsonProperty("perOperation")
    private int perOperation;

    @JsonProperty("grosPerDay")
    private int grosPerDay;

    @JsonProperty("grosPerOperation")
    private int grosPerOperation;
}