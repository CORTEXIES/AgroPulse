package com.github.cortex.database.dto.classifcation;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.github.cortex.database.dto.agro.AgronomistEntity;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "unclassified_messages")
public class UnclassifiedMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "agronomist_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "unclassified_message_agronomist_id_fkey"))
    private AgronomistEntity agronomist;

    @Column(length = 5000)
    private String report;

    @Column(length = 500)
    private String photoUrl;

    private LocalDateTime receivedAt;

    @Enumerated(EnumType.STRING)
    private UnclassifiedMessageStatus status;

    public UnclassifiedMessageEntity(
            AgronomistEntity agronomist,
            String report,
            String photoUrl,
            LocalDateTime receivedAt,
            UnclassifiedMessageStatus status
    ) {
        this.agronomist = agronomist;
        this.report = report;
        this.photoUrl = photoUrl;
        this.receivedAt = receivedAt;
        this.status = status;
    }
}