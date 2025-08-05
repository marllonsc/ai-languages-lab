package com.msc.spring_ai.controller;

import com.msc.spring_ai.data.Achievement;
import com.msc.spring_ai.data.Player;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final ChatClient chatClient;

    public PlayerController(ChatClient.Builder cliBuilder) {
        this.chatClient = cliBuilder.build();
    }

    @GetMapping("/info")
    public List<Player> getPlayerAchievemente(@RequestParam String name){

        BeanOutputConverter<List<Player>> converter
                = new BeanOutputConverter<>(new ParameterizedTypeReference<>() {
        });

        String message = """
                Generate a list of Career achievements for te sports person {sports}.
                Include the Player as the key and achievements as the value of it
                {format}
                """;

        PromptTemplate template = new PromptTemplate(message);

        Prompt prompt = template.create(Map.of("sports",name,"format",converter.getFormat()));

        /*ChatResponse response =  chatClient.prompt(prompt).call().chatResponse();

        assert response != null;
        return response.getResult().getOutput().getText();*/

        Generation result = Objects.requireNonNull(
                chatClient.prompt(prompt)
                        .call()
                        .chatResponse())
                .getResult();

        return converter.convert(result.getOutput().getText());
        //return result.getOutput().getText();
    }

    @GetMapping("/achievement/player")
    public List<Achievement> getAchievement(@RequestParam String name){

        String message = """
                Provide a List of Achievements for {player}
                """;

        PromptTemplate template = new PromptTemplate(message);

        Prompt prompt = template.create(Map.of("player",name));

        String rawContent = chatClient.prompt(prompt)
                .call().content();
               // .entity(new ParameterizedTypeReference<List<Achievement>>() {
                //});

       // return result.getOutput().getText();

        //List String
        /*assert rawContent != null;
        return Arrays.stream(rawContent.split("\\r?\\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());*/

        List<Achievement> achievements = new ArrayList<>();
        Pattern pattern = Pattern.compile("^(\\d+)\\.\\s+(.*)$");

        for (String line : rawContent.split("\\r?\\n")) {
            Matcher matcher = pattern.matcher(line.trim());
            if (matcher.matches()) {
                int number = Integer.parseInt(matcher.group(1));
                String description = matcher.group(2);
                achievements.add(new Achievement(number, description));
            }
        }

        return achievements;
    }
}
