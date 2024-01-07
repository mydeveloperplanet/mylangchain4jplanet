package com.mydeveloperplanet.mylangchain4jplanet;

import static dev.langchain4j.data.document.FileSystemDocumentLoader.loadDocument;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

public class EmbeddingBasic {

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

        // Read and split the documents in segments of 500 chunks
        Document springsteenDiscography = loadDocument(toPath("example-files/Bruce_Springsteen_discography.pdf"));
        Document springsteenSongList = loadDocument(toPath("example-files/List_of_songs_recorded_by_Bruce_Springsteen.pdf"));
        ArrayList<Document> documents = new ArrayList<>();
        documents.add(springsteenDiscography);
        documents.add(springsteenSongList);

        DocumentSplitter documentSplitter = DocumentSplitters.recursive(500, 0);
        List<TextSegment> documentSegments = documentSplitter.splitAll(documents);

        // Embed the segments
        Response<List<Embedding>> embeddings = embeddingModel.embedAll(documentSegments);
        embeddingStore.addAll(embeddings.content(), documentSegments);

        // Embed the question and find relevant segments
        Embedding queryEmbedding = embeddingModel.embed(question).content();
        List<EmbeddingMatch<TextSegment>> embeddingMatch = embeddingStore.findRelevant(queryEmbedding,1);
        System.out.println(embeddingMatch.get(0).score());
        System.out.println(embeddingMatch.get(0).embedded().text());
        System.out.println(embeddingMatch.get(0).embedded().metadata());

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
