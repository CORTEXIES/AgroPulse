package com.github.cortex.database.dto.classifcation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "classified_messages")
public class MessageClassificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    private String department;
    private String operation;
    private String plant;

    private int perDay;
    private int perOperation;
    private int grosPerDay;
    private int grosPerOperation;

    @Enumerated(EnumType.STRING)
    private ClassificationStatus classificationStatus = ClassificationStatus.NEW;

    public MessageClassificationEntity(
            LocalDateTime date,
            String department,
            String operation,
            String plant,
            int perDay,
            int perOperation,
            int grosPerDay,
            int grosPerOperation
    ) {
        this.date = date;
        this.department = department;
        this.operation = operation;
        this.plant = plant;
        this.perDay = perDay;
        this.perOperation = perOperation;
        this.grosPerDay = grosPerDay;
        this.grosPerOperation = grosPerOperation;
    }
}