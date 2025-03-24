package com.i4u.delivery.presentation.dtos.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResDto {
	private UUID messageId;
	private String messageContent;
	private String slackId;
}
