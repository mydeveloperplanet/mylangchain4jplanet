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
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.localai.LocalAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

public class EmbeddingBasicMarkdownWithLlm {

    public static void main(String[] args) {

        askQuestion("on which album was \"adam raised a cain\" originally released?");
        askQuestion("what is the highest chart position of \"Greetings from Asbury Park, N.J.\" in the US?");
        askQuestion("what is the highest chart position of the album \"tracks\" in canada?");
        askQuestion("in which year was \"Highway Patrolman\" released?");
        askQuestion("in which year was \"Higway Patrolman\" released?"); // with typo
        askQuestion("who produced \"all or nothin' at all\"");
    }

    private static void askQuestion(String question) {
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

        ConversationalRetrievalChain chain = ConversationalRetrievalChain.builder()
                .chatLanguageModel(model)
                .retriever(EmbeddingStoreRetriever.from(embeddingStore, embeddingModel))
                .build();

        String answer = chain.execute(question);
        System.out.println(answer);
    }

    private static Path toPath(String fileName) {
        try {
            URL fileUrl = EmbeddingBasicMarkdownWithLlm.class.getClassLoader().getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
