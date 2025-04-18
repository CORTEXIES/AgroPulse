package com.github.cortex.database.dto.classifcation;

import com.github.cortex.database.dto.agro.AgronomistEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

//    @Lob
//    private byte[] photo;

    private LocalDateTime receivedAt;

    @Enumerated(EnumType.STRING)
    private UnclassifiedMessageStatus status;

    public UnclassifiedMessageEntity(
            AgronomistEntity agronomist,
            String report,
            LocalDateTime receivedAt,
            UnclassifiedMessageStatus status
    ) {
        this.agronomist = agronomist;
        this.report = report;
        this.receivedAt = receivedAt;
        this.status = status;
    }
}