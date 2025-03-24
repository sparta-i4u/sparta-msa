package com.i4u.message.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
public class CommandLineRunner implements org.springframework.boot.CommandLineRunner{
    private final AiService aiService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Gemini API 테스트 시작 ===");

        // 간단한 질문으로 Gemini API 테스트
        String question = "안녕하세요. 당신은 누구인가요?";
        System.out.println("질문: " + question);

        String response = aiService.askQuestion(question);
        System.out.println("Gemini 응답: " + response);

        System.out.println("=== Gemini API 테스트 종료 ===");
    }
}
