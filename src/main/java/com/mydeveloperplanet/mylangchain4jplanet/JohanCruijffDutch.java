package com.mydeveloperplanet.mylangchain4jplanet;

import java.util.ArrayList;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.localai.LocalAiChatModel;
import dev.langchain4j.model.output.Response;

public class JohanCruijffDutch {

    public static void main(String[] args) {
        ChatLanguageModel model = LocalAiChatModel.builder()
                .baseUrl("http://localhost:8080")
                .modelName("lunademo")
                .temperature(0.0)
                .build();

        SystemMessage responseInDutch = new SystemMessage("You are a helpful assistant. Antwoord altijd in het Nederlands.");
        UserMessage question = new UserMessage("who is Johan Cruijff?");
        var chatMessages = new ArrayList<ChatMessage>();
        chatMessages.add(responseInDutch);
        chatMessages.add(question);

        Response<AiMessage> response = model.generate(chatMessages);
        System.out.println(response.content());
    }

}
