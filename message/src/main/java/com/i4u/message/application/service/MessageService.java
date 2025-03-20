package com.i4u.message.application.service;

import com.i4u.message.application.dto.MessageResDto;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MessageService {
    @Value("${slack.token}")
    private String slackToken;

    public MessageResDto sendMessage(String message, String channelId) throws IOException, SlackApiException {
        Slack slack = Slack.getInstance();
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channelId)
                .token(slackToken)
                .text(message)
                .build();

        ChatPostMessageResponse response = slack.methods().chatPostMessage(request);

        if (!response.isOk()) {
            throw new RuntimeException("메시지 전송 실패: " + response.getError());
        }

        return MessageResDto.builder()
                .messageId(response.getMessage().getTs())
                .messageContent(message)
                .slackId(channelId)
                .build();
    }
}
