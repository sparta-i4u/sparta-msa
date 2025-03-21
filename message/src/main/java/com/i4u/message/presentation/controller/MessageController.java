package com.i4u.message.presentation.controller;

import com.i4u.common.utils.CommonResponse;
import com.i4u.message.application.dto.AIMessageReqDto;
import com.i4u.message.application.dto.MessageResDto;
import com.i4u.message.application.service.MessageService;
import com.slack.api.methods.SlackApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("slack-test")
    public ResponseEntity<CommonResponse<MessageResDto>> sendMessage(String message, String slackId) {
        try {
            MessageResDto messageResDto = messageService.sendMessage(message, slackId);
            return ResponseEntity.ok(CommonResponse.success(messageResDto, "메시지가 성공적으로 전송되었습니다!"));
        } catch (IOException | SlackApiException e) {
            return ResponseEntity.badRequest().body(CommonResponse.fail("F000", "메시지 전송 중 오류가 발생했습니다.", 400));
        }
    }


    @PostMapping("AI-slack")
    public ResponseEntity<CommonResponse<MessageResDto>> sendAIMessage(@RequestBody AIMessageReqDto aiMessageReqDto) {
        try {
            MessageResDto messageResDto = messageService.sendAIMessage(aiMessageReqDto);
            return ResponseEntity.ok(CommonResponse.success(messageResDto, "메시지가 성공적으로 전송되었습니다!"));
        } catch (IOException | SlackApiException e) {
            return ResponseEntity.badRequest().body(CommonResponse.fail("F000", "AI 메시지 전송 중 오류가 발생했습니다.", 400));
        }
    }
}
