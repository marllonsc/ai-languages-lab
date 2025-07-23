package com.msc.spring_ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder cliBuilder) {
        this.chatClient = cliBuilder.build();
    }

    @GetMapping
    public String chat(@RequestParam String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }
}

