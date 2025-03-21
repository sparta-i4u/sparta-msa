package com.i4u.message.application.service;

import com.i4u.message.application.dto.AIMessageReqDto;
import com.i4u.message.application.dto.MessageResDto;
import com.i4u.message.domain.model.AI;
import com.i4u.message.domain.model.Message;
import com.i4u.message.domain.repository.AIRepository;
import com.i4u.message.domain.repository.MessageRepository;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final AIRepository aiRepository;
    private final AIService aiService;

    @Value("${slack.token}")
    private String slackToken;

    public MessageResDto sendMessage(String message, String slackId) throws IOException, SlackApiException {
        Slack slack = Slack.getInstance();
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(slackId)
                .token(slackToken)
                .text(message)
                .build();

        ChatPostMessageResponse response = slack.methods().chatPostMessage(request);

        if (!response.isOk()) {
            throw new RuntimeException("메시지 전송 실패: " + response.getError());
        }

        return MessageResDto.builder()
                .messageContent(message)
                .slackId(slackId)
                .build();
    }

    public MessageResDto sendAIMessage(AIMessageReqDto aiMessageReqDto) throws IOException, SlackApiException {
        // AI 응답 생성
        String question = aiMessageReqDto.getAi();
        String aiResponse = aiService.generateResponse(question);

        // AI 엔티티 저장
        AI ai = AI.builder()
                .aiName("Gemini")
                .question(question)
                .answer(aiResponse)
                .build();

        aiRepository.save(ai);

        // 슬랙 메시지 전송
        String slackId = aiMessageReqDto.getSlackId();
        sendMessage(aiResponse, slackId);

        // 메시지 엔티티 저장
        Message message = Message.builder()
                .messageContent(aiResponse)
                .slackId(slackId)
                .build();

        Message savedmessage = messageRepository.save(message);

        return MessageResDto.from(savedmessage);
    }
}
