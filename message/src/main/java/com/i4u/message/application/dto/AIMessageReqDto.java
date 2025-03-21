package com.i4u.message.application.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIMessageReqDto {
    private String ai;
    private String slackId;
}
