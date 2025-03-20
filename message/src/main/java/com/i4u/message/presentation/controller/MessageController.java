package com.i4u.message.presentation.controller;

import com.i4u.common.utils.CommonResponse;
import com.i4u.message.application.dto.MessageResDto;
import com.i4u.message.application.service.MessageService;
import com.slack.api.methods.SlackApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping
    public ResponseEntity<CommonResponse<MessageResDto>> sendMessage(String message, String channelId) {
        try {
            MessageResDto messageResDto = messageService.sendMessage(message, channelId);
            return ResponseEntity.ok(CommonResponse.success(messageResDto, "메시지가 성공적으로 전송되었습니다!"));
        } catch (IOException | SlackApiException e) {
            return ResponseEntity.badRequest().body(CommonResponse.fail());
        }
    }

}
