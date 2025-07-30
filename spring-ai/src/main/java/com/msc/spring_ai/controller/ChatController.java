package com.msc.spring_ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/celeb-details.st")
    private Resource celebPrompt;

    public ChatController(ChatClient.Builder cliBuilder) {
        this.chatClient = cliBuilder.build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String userMessage) {
        return Objects.requireNonNull(chatClient.prompt()
                        .user(userMessage)
                        .call()
                        .chatResponse())
                .getResult()
                .getOutput()
                .getText();
    }

    @GetMapping("/celeb")
    public String getCelebDetail(@RequestParam String name){
        String message = """
                List the details of the Famous personality {name}
                along with their Carrier achievements.
                Show the details in the readable format
                """;

        PromptTemplate template = new PromptTemplate(celebPrompt);

        Prompt prompt = template.create(
                Map.of("name",name)
        );

        return Objects.requireNonNull(
                chatClient.prompt(prompt)
                        .call()
                        .chatResponse())
                .getResult()
                .getOutput()
                .getText();
    }

    @GetMapping("/sports")
    public String getSportsDetail(@RequestParam String name){
        String message = """
                List the details of the Sports %s
                along with their Rules and Regulations.
                Show the details in the readable format
                """;

        String systemMessage = """
               you are a smart Virtual Assistant.
               Your task is to give the details about the Sports.
               If someone ask about something else and you do not know the answer.
               Just sy that you do not know the answer.
               """;

        UserMessage userMessage = new UserMessage((String.format(message,name)));

        SystemMessage systemMessage1 = new SystemMessage(systemMessage);

        Prompt prompt = new Prompt(List.of(userMessage,systemMessage1));

        return Objects.requireNonNull(chatClient.prompt(prompt).call().chatResponse()).getResult().getOutput().getText();
    }


}

