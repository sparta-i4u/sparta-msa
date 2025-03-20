package com.i4u.message.application.dto;

import com.i4u.message.domain.model.Message;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResDto {
    private String messageId;
    private String messageContent;
    private String slackId;

    public static MessageResDto from(Message message) {
        return MessageResDto.builder()
                .messageId(message.getMessageId())
                .messageContent(message.getMessageContent())
                .slackId(message.getSlackId())
                .build();
    }
}
