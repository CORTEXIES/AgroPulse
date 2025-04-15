package com.github.cortex.database.dto.agro;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "agronomist")
public class AgronomistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(name = "telegram_id")
    private String telegramId;

    public AgronomistEntity(String fullName, String telegramId) {
        this.fullName = fullName;
        this.telegramId = telegramId;
    }
}