package com.i4u.message.application.service;

import com.i4u.common.utils.CommonResponse;
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
import com.i4u.message.infrastructure.dto.ShortestPathResDto;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        // 허브 가져오기
        CommonResponse<HubDto> hubDto = hubClient.getHubInfos(request.getSupplierHubId(), request.getRecipientHubId());
        ResponseEntity<CommonResponse<ShortestPathResDto>> hubConnectionDto = hubClient.getShortestPath(hubDto.getData().getSupplierHubName(), hubDto.getData().getRecipientHubName());
        List<ShortestPathResDto.PathHubDto> path = hubConnectionDto.getBody().getData().getPath();

        String departure = path.get(0).getHubName();
        String waypoints = path.size() > 1
                ? path.subList(1, path.size()).stream()
                .map(ShortestPathResDto.PathHubDto::getHubName)
                .collect(Collectors.joining(" -> "))
                : "";

        // 주문 정보를 기반으로 AI에 질문할 문자열 생성
        String aiQuestion = String.format(
            "다음 고객 주문 정보를 기반으로 발송 시한을 계산해 주세요 (발송시한만 답변):\n" +
                "**발송 시한은 반드시 'YYYY-MM-DD HH:mm' 형식으로 답변해 주세요.**\n" +
                "발송 가능 시간: 매일 09:00 - 18:00\n" +
                "배송 담당자 근무시간: 09 - 18\n" +
                "공급업체 허브 위치: 위도 %f, 경도 %f\n" +
                "수신자 허브 위치: 위도 %f, 경도 %f\n\n" +
                "최종 도착 주소: %s\n" +
                "주문 정보:\n" +
                "- 주문 ID: %s\n" +
                "- 제품명: %s\n" +
                "- 제품 수량: %s\n" +
                "- 요구사항: %s (요구사항을 반영하여 발송 시한을 계산해 주세요.)\n" +
                "**요구사항을 반드시 고려하여 발송 시한을 계산하세요.**",

            hubDto.getData().getSupplierHubLatitude(),
            hubDto.getData().getSupplierHubLongitude(),
            hubDto.getData().getRecipientHubLatitude(),
            hubDto.getData().getRecipientHubLongitude(),
            request.getAddress(),

            request.getOrderId(),
            request.getProductName(),
            request.getProductQuantity(),
            request.getRequirement()
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

        String slackId = null;
        if (hubDto.getData() != null) {
            System.out.println(hubDto.getData().getSupplierHubManagerId());
            ConfirmUserResponse confirmUserResponse = authClient.confirmUser(hubDto.getData().getSupplierHubManagerId());
            slackId = confirmUserResponse.getUserSlackId();
        }

        // 슬랙 메시지 전송
        String sendMessageToHubManager = makeMessage(aiResponse, request, departure, waypoints);
        sendMessage(sendMessageToHubManager, slackId);

        // 메시지 엔티티 저장
        Message message = Message.builder()
                .messageContent(aiResponse)
                .slackId(slackId)
                .build();

        Message savedMessage = messageRepository.save(message);

        return MessageResDto.from(savedMessage);
    }

    private String makeMessage(String aiResponse, AIMessageReqDto request, String departure, String waypoints) {
        String format = "주문 번호 : %s%n" +
            "주문자 정보 : %s / %s%n" +
            "상품 정보 : %s%n" +
            "요청 사항 : %s%n" +
            "출발지 : %s%n" +
            "경유지 : %s%n" +
            "도착지 : %s%n" +
            "배송담당자 : %s / %s%n%n" +
            "최종 발송 시한은 %s시 입니다.";

        String result = String.format(format,
            String.valueOf(request.getOrderId()),
            request.getRecipientEmail(), request.getRecipientSlackId(),
            request.getProductName() + " " + String.valueOf(request.getProductQuantity()),
            request.getRequirement(),
            departure,
            waypoints,
            request.getAddress(),
            request.getShipperEmail(), request.getShipperSlackId(),
            aiResponse.trim()
        );

        System.out.println(result);

        return result;
    }
}

