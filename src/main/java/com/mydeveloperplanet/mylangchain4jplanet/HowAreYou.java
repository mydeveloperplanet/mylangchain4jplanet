package com.mydeveloperplanet.mylangchain4jplanet;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.localai.LocalAiChatModel;
public class HowAreYou {

    public static void main(String[] args) {
        ChatLanguageModel model = LocalAiChatModel.builder()
                .baseUrl("http://localhost:8080")
                .modelName("lunademo")
                .temperature(0.9)
                .build();

        String answer = model.generate("How are you?");
        System.out.println(answer);
    }

}
