package com.mydeveloperplanet.mylangchain4jplanet;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.localai.LocalAiChatModel;

public class JohanCruijff {

    public static void main(String[] args) {
        ChatLanguageModel model = LocalAiChatModel.builder()
                .baseUrl("http://localhost:8080")
                .modelName("lunademo")
                .temperature(0.0)
                .build();

        String answer = model.generate("who is Johan Cruijff?");
        System.out.println(answer);
    }

}
