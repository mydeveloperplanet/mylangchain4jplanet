package com.mydeveloperplanet.mylangchain4jplanet;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.CompletableFuture;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.localai.LocalAiStreamingChatModel;
import dev.langchain4j.model.output.Response;

public class JohanCruijffStreaming {

    public static void main(String[] args) throws Exception {
        StreamingChatLanguageModel model = LocalAiStreamingChatModel.builder()
                .baseUrl("http://localhost:8080")
                .modelName("lunademo")
                .temperature(0.0)
                .build();

        StringBuilder answerBuilder = new StringBuilder();
        CompletableFuture<String> futureAnswer = new CompletableFuture<>();

        model.generate("who is Johan Cruijff?", new StreamingResponseHandler<AiMessage>() {

            @Override
            public void onNext(String token) {
                answerBuilder.append(token);
                System.out.println(token);
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                futureAnswer.complete(answerBuilder.toString());
            }

            @Override
            public void onError(Throwable error) {
                futureAnswer.completeExceptionally(error);
            }
        });

        String answer = futureAnswer.get(90, SECONDS);
        System.out.println(answer);

    }

}
