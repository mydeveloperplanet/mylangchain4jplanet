package com.mydeveloperplanet.mylangchain4jplanet;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.localai.LocalAiChatModel;

public class BruceSpringsteen {

    public static void main(String[] args) {
        askQuestion("on which album was \"adam raised a cain\" originally released?");
        askQuestion("what is the highest chart position of \"Greetings from Asbury Park, N.J.\" in the US?");
        askQuestion("what is the highest chart position of the album \"tracks\" in canada?");
        askQuestion("in which year was \"Highway Patrolman\" released?");
        askQuestion("who produced \"all or nothin' at all?\"");
    }

    private static void askQuestion(String question) {
        ChatLanguageModel model = LocalAiChatModel.builder()
                .baseUrl("http://localhost:8080")
                .modelName("lunademo")
                .temperature(0.0)
                .build();

        String answer = model.generate(question);
        System.out.println(answer);
    }

}
