package com.i4u.message.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i4u.message.application.dto.AIMessageReqDto;
import com.i4u.message.application.dto.MessageResDto;
import com.i4u.message.application.service.AiService;
import com.i4u.message.application.service.MessageService;
import com.i4u.message.domain.model.Message;
import com.slack.api.methods.SlackApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    @MockitoBean
    private AiService aiService;

    @Test
    @DisplayName("슬랙 메시지 전송 테스트")
    public void sendMessageTest() throws Exception {
        // given
        String message = "테스트 메시지";
        String slackId = "U08K0NRDRT3";
        MessageResDto mockResponse = MessageResDto.builder()
                .messageContent(message)
                .slackId(slackId)
                .build();

        // when
        when(messageService.sendMessage(anyString(), anyString())).thenReturn(mockResponse);

        // then
        mockMvc.perform(get("/api/v1/messages/slack-test")
                .param("message", message)
                .param("slackId", slackId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.messageContent").value(message))
            .andExpect(jsonPath("$.data.slackId").value(slackId));
    }

    @Test
    @DisplayName("슬랙 메시지 전송 실패 테스트")
    public void sendMessageFailTest() throws Exception {
        // given
        String message = "테스트 메시지";
        String slackId = "TESTSLACKID";

        // when
        when(messageService.sendMessage(anyString(), anyString())).thenThrow(new IOException("메시지 전송 실패"));

        // then
        mockMvc.perform(get("/api/v1/messages/slack-test")
                .param("message", message)
                .param("slackId", slackId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("F000"));
    }

    @Test
    @DisplayName("AI 응답 생성 테스트")
    public void generateAIResponseTest() throws Exception {
        // given
        String message = "AI에게 질문하는 메시지";
        String aiResponse = "AI의 응답 메시지";

        // when
        when(aiService.generateResponse(anyString())).thenReturn(aiResponse);

        // then
        mockMvc.perform(get("/api/v1/messages/AI-test")
                .param("message", message))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").value(aiResponse));
    }

    @Test
    @DisplayName("AI 응답 생성 실패 테스트")
    public void generateAIResponseFailTest() throws Exception {
        // given
        String message = "AI에게 질문하는 메시지";

        // when
        when(aiService.generateResponse(anyString())).thenThrow(new RuntimeException("AI 응답 생성 실패"));

        // then
        mockMvc.perform(get("/api/v1/messages/AI-test")
                .param("message", message))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("F000"));
    }

    @Test
    @DisplayName("AI 슬랙 메시지 전송 테스트")
    public void sendAIMessageTest() throws Exception {
        // given
        UUID orderId = UUID.randomUUID();
        UUID supplierHubId = UUID.randomUUID();
        UUID recipientHubId = UUID.randomUUID();

        AIMessageReqDto request = AIMessageReqDto.builder()
                .orderId(orderId)
                .recipientEmail("recipient@example.com")
                .recipientSlackId("RECIPIENT_SLACK_ID")
                .productName("테스트 상품")
                .productQuantity(10)
                .requirement("테스트 요구사항")
                .supplierHubId(supplierHubId)
                .recipientHubId(recipientHubId)
                .shipperEmail("shipper@example.com")
                .shipperSlackId("SHIPPER_SLACK_ID")
                .build();

        UUID messageId = UUID.randomUUID();
        MessageResDto mockResponse = MessageResDto.builder()
                .messageId(messageId)
                .messageContent("AI 응답 메시지")
                .slackId("SLACKID")
                .build();

        // when
        when(messageService.sendAIMessage(any(AIMessageReqDto.class))).thenReturn(mockResponse);

        // then
        mockMvc.perform(post("/api/v1/messages/AI-slack")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.messageId").value(messageId.toString()))
            .andExpect(jsonPath("$.data.messageContent").value("AI 응답 메시지"))
            .andExpect(jsonPath("$.data.slackId").value("SLACKID"));
    }

}