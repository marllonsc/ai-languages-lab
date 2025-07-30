package com.msc.spring_ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final ChatClient chatClient;

    public PlayerController(ChatClient.Builder cliBuilder) {
        this.chatClient = cliBuilder.build();
    }

    @GetMapping("/info")
    public String getPlayerAchievemente(@RequestParam String name){
        String message = """
                Generate a list of Career achievements for te sports person {sports}.
                Include the Player as the key and achievements as the value of it
                """;

        PromptTemplate template = new PromptTemplate(message);

        Prompt prompt = template.create(Map.of("sports",name));

        ChatResponse response =  chatClient.prompt(prompt).call().chatResponse();

        assert response != null;
        return response.getResult().getOutput().getText();
    }
}
