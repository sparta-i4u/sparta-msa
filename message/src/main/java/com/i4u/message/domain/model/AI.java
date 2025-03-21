package com.i4u.message.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "p_ai")
public class AI {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID aiId;

    @Column(nullable = false)
    private String aiName;

    @Lob
    @Column(nullable = false, columnDefinition = "CLOB")
    private String question;

    @Lob
    @Column(nullable = false, columnDefinition = "CLOB")
    private String answer;
}
