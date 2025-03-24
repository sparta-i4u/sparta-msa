package com.i4u.message.application.service;

import com.i4u.message.application.dto.AIMessageReqDto;
import com.i4u.message.application.dto.MessageResDto;
import com.i4u.message.domain.model.AI;
import com.i4u.message.domain.model.Message;
import com.i4u.message.domain.repository.AIRepository;
import com.i4u.message.domain.repository.MessageRepository;
import com.i4u.message.infrastructure.client.AuthClient;
import com.i4u.message.infrastructure.client.HubClient;
import com.i4u.message.infrastructure.dto.ConfirmUserResponse;
import com.i4u.message.infrastructure.dto.HubDto;
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
    private final AiService aiService;
    private final HubClient hubClient;
    private final AuthClient authClient;

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

    public MessageResDto sendAIMessage(AIMessageReqDto request) throws IOException, SlackApiException {
        // 주문 정보를 기반으로 AI에 질문할 문자열 생성
        String aiQuestion = String.format(
                "다음 고객 주문 정보를 바탕으로 물류 시스템을 고려해서 업체의 발송 시한을 계산해서 알려주세요 (발송시한만 답변):\n" +
                        "배송 담당자 근무시간:  09 - 18 \n" +
                        "주문 ID: %s\n" +
                        "제품명: %s\n" +
                        "제품 수량: %s\n" +
                        "요구사항: %s\n" +
                        "공급업체 허브 ID: %s\n" +
                        "수신자 허브 ID: %s\n" +
                        "발송자 이메일: %s\n" +
                        "수신자 이메일: %s",
                request.getOrderId(),
                request.getProductName(),
                request.getProductQuantity(),
                request.getRequirement(),
                request.getSupplierHubId(),
                request.getRecipientHubId(),
                request.getShipperEmail(),
                request.getRecipientEmail()
        );

        // AI 응답 생성
        String aiResponse = aiService.generateResponse(aiQuestion);

        // AI 엔티티 저장
        AI ai = AI.builder()
                .aiName("Gemini")
                .question(aiQuestion)
                .answer(aiResponse)
                .build();

        aiRepository.save(ai);

        // 허브 가져오기
        HubDto hubDto = hubClient.getHubById(request.getSupplierHubId());

        String slackId = null;
        if (hubDto.getManagerId() != null) {
            ConfirmUserResponse confirmUserResponse = authClient.confirmUser(hubDto.getManagerId());
            slackId = confirmUserResponse.getUserSlackId();
        }

        // 슬랙 메시지 전송
        sendMessage(aiResponse, slackId);

        // 메시지 엔티티 저장
        Message message = Message.builder()
                .messageContent(aiResponse)
                .slackId(slackId)
                .build();

        Message savedMessage = messageRepository.save(message);

        return MessageResDto.from(savedMessage);
    }
}

