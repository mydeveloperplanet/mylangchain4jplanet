package com.mydeveloperplanet.mylangchain4jplanet;

import static dev.langchain4j.data.document.FileSystemDocumentLoader.loadDocuments;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

public class EmbeddingBasicMarkdownAlternativeQuestions {

    public static void main(String[] args) {
        askQuestion("what is the original release of \"adam raised a cain\"?");
        askQuestion("in which year was \"Highway Patrolman\" released?");
        askQuestion("in which year was the song \"Highway Patrolman\" released?");
        askQuestion("in which year was the song \"Highway Patrolman\" of the album \"Nebraska\" released?");
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

        Embedding queryEmbedding = embeddingModel.embed(question).content();
        List<EmbeddingMatch<TextSegment>> relevantMatches = embeddingStore.findRelevant(queryEmbedding,5);
        for (EmbeddingMatch<TextSegment> embeddingMatch : relevantMatches) {
            System.out.println(embeddingMatch.score());
            System.out.println(embeddingMatch.embedded().text());
            System.out.println(embeddingMatch.embedded().metadata());
        }
    }

    private static Path toPath(String fileName) {
        try {
            URL fileUrl = ChatWithDocumentsBruceSpringsteenFinalSolution.class.getClassLoader().getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
