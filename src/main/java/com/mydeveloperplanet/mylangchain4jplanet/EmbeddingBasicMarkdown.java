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

public class EmbeddingBasicMarkdown {

    public static void main(String[] args) {
        askQuestion("on which album was \"adam raised a cain\" originally released?");
        askQuestion("what is the highest chart position of \"Greetings from Asbury Park, N.J.\" in the US?");
        askQuestion("what is the highest chart position of the album \"tracks\" in canada?");
        askQuestion("in which year was \"Highway Patrolman\" released?");
        askQuestion("who produced \"all or nothin' at all\"?");
    }

    private static void askQuestion(String question) {
        System.out.println("==================================================");
        System.out.println(question);
        System.out.println("==================================================");

        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Read the documents from the directory markdown-files
        List<Document> documents = loadDocuments(toPath("markdown-files"));

        // Read every line from the document in splittedDocument
        // Retrieve the data from the table in dataOnly
        // Save the header of the table in header
        // Create a TextSegment for every row in the table and add the header to every TextSegment
        List<TextSegment> segments = new ArrayList<>();
        for (Document document : documents) {
            String[] splittedDocument = document.text().split("\n");
            String[] dataOnly = Arrays.copyOfRange(splittedDocument, 2, splittedDocument.length);
            String header = splittedDocument[0] + "\n" + splittedDocument[1] + "\n";

            for (String splittedLine : dataOnly) {
                segments.add(TextSegment.from(header + splittedLine, document.metadata()));
            }
        }

        // Embed the segments
        Response<List<Embedding>> embeddings = embeddingModel.embedAll(segments);
        embeddingStore.addAll(embeddings.content(), segments);

        // Embed the question and find relevant segments
        Embedding queryEmbedding = embeddingModel.embed(question).content();
        List<EmbeddingMatch<TextSegment>> relevantMatches = embeddingStore.findRelevant(queryEmbedding,1);
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
