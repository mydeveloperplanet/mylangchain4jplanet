package com.mydeveloperplanet.mylangchain4jplanet;

import static dev.langchain4j.data.document.FileSystemDocumentLoader.loadDocuments;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.localai.LocalAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

public class EmbeddingBasicMarkdownWithLlmFinalSolution {

    public static void main(String[] args) {
        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new SystemMessage("Use the provided documents to answer the questions."));
        chatMessages.add(new SystemMessage("If the answer cannot be found in the documents, write \"I could not find an answer\"."));

        askQuestion("on which album was \"adam raised a cain\" originally released?", chatMessages);
        askQuestion("what is the highest chart position of \"Greetings from Asbury Park, N.J.\" in the US?", chatMessages);
        askQuestion("what is the highest chart position of the album \"tracks\" in canada?", chatMessages);
        askQuestion("in which year was \"Highway Patrolman\" released?", chatMessages);
        askQuestion("in which year was \"Higway Patrolman\" released?", chatMessages); // with typo
        askQuestion("who produced \"all or nothin' at all\"?", chatMessages);
    }

    private static void askQuestion(String question, List<ChatMessage> chatMessages) {
        System.out.println("==================================================");
        System.out.println(question);
        System.out.println("==================================================");

        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        List<Document> documents = loadDocuments(toPath("markdown-files"));

        List<TextSegment> segments = new ArrayList<>();
        for (Document document : documents) {
            String[] splittedDocument = document.text().split("\n");
            String[] dataOnly = Arrays.copyOfRange(splittedDocument, 2, splittedDocument.length);
            String header = splittedDocument[0] + "\n" + splittedDocument[1] + "\n";

            for (String splittedLine : dataOnly) {
                segments.add(TextSegment.from(header + splittedLine, document.metadata()));
            }
        }

        Response<List<Embedding>> embeddings = embeddingModel.embedAll(segments);
        embeddingStore.addAll(embeddings.content(), segments);

        System.out.println("embedding done");

        ChatLanguageModel model = LocalAiChatModel.builder()
                .baseUrl("http://localhost:8080")
                .modelName("lunademo")
                .temperature(0.0)
                .timeout(Duration.ofMinutes(5))
                .build();

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();

        for (ChatMessage chatMessage : chatMessages) {
            chatMemory.add(chatMessage);
        }

        ConversationalRetrievalChain chain = ConversationalRetrievalChain.builder()
                .chatLanguageModel(model)
                .chatMemory(chatMemory)
                .retriever(EmbeddingStoreRetriever.from(embeddingStore, embeddingModel))
                .build();

        String answer = chain.execute(question);
        System.out.println(answer);
    }

    private static Path toPath(String fileName) {
        try {
            URL fileUrl = EmbeddingBasicMarkdownWithLlmFinalSolution.class.getClassLoader().getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
