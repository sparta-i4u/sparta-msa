package com.i4u.message.domain.model;

import com.i4u.common.entity.Basic;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "p_message")
public class Message extends Basic {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    private String messageId;

    @Column(nullable = false)
    private String messageContent;

    @Column(nullable = false)
    private String slackId;
}
