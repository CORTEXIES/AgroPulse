package com.github.cortex.database.dto.agro;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

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