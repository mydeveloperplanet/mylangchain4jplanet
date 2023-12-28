package com.mydeveloperplanet.mylangchain4jplanet;

import static dev.langchain4j.data.document.FileSystemDocumentLoader.loadDocument;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.localai.LocalAiChatModel;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

public class ChatWithDocumentsBruceSpringsteenQ3WithChatMemory {

    public static void main(String[] args) {
        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        // Attempt #1
        chatMessages.add(new SystemMessage("Use the provided documents Bruce_Springsteen_discography.pdf and List_of_songs_recorded_by_Bruce_Springsteen.pdf to answer the questions."));
        chatMessages.add(new SystemMessage("If the answer cannot be found in the documents, write \"I could not find an answer\"."));
        askQuestion("what is the highest chart position of the album \"tracks\" in canada?", chatMessages);

        // Attempt #2
        chatMessages.clear();
        chatMessages.add(new SystemMessage("Use the provided documents Bruce_Springsteen_discography.pdf and List_of_songs_recorded_by_Bruce_Springsteen.pdf to answer the questions."));
        chatMessages.add(new SystemMessage("Do not provide any additional information."));
        chatMessages.add(new SystemMessage("If the answer cannot be found in the documents, write \"I could not find an answer\"."));
        askQuestion("what is the highest chart position of the album \"tracks\" in canada?", chatMessages);

        // Attempt #3
        chatMessages.clear();
        chatMessages.add(new SystemMessage("Use the provided documents Bruce_Springsteen_discography.pdf and List_of_songs_recorded_by_Bruce_Springsteen.pdf to answer the questions."));
        chatMessages.add(new SystemMessage("If the answer cannot be found in the documents, write \"I could not find an answer\"."));
        askQuestion("what is the highest chart position of the album \"greetings from Asbury Park, N.J.\" of Bruce Springsteen in germany?", chatMessages);

        // Attempt #4
        chatMessages.clear();
        chatMessages.add(new SystemMessage("Use the provided documents Bruce_Springsteen_discography.pdf and List_of_songs_recorded_by_Bruce_Springsteen.pdf to answer the questions."));
        chatMessages.add(new SystemMessage("If the answer cannot be found in the documents, write \"I could not find an answer\"."));
        askQuestion("what is the highest chart position of the album \"greetings from Asbury Park, N.J.\" of Bruce Springsteen in Australia?", chatMessages);

        // Attempt #5
        chatMessages.clear();
        chatMessages.add(new SystemMessage("Use the provided documents Bruce_Springsteen_discography.pdf and List_of_songs_recorded_by_Bruce_Springsteen.pdf to answer the questions."));
        chatMessages.add(new SystemMessage("If the answer cannot be found in the documents, write \"I could not find an answer\"."));
        askQuestion("what is the peak chart position of the album \"greetings from Asbury Park, N.J.\" of Bruce Springsteen in Australia?", chatMessages);

        // Attempt #6
        chatMessages.clear();
        chatMessages.add(new SystemMessage("Use the provided documents Bruce_Springsteen_discography.pdf and List_of_songs_recorded_by_Bruce_Springsteen.pdf to answer the questions."));
        chatMessages.add(new SystemMessage("If the answer cannot be found in the documents, write \"I could not find an answer\"."));
        askQuestion("what is the highest chart position of the album \"Greetings from Asbury Park, N.J.\" of Bruce Springsteen in the US?", chatMessages);

    }

    private static void askQuestion(String question, List<ChatMessage> chatMessages) {
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(500, 0))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        Document springsteenDiscography = loadDocument(toPath("example-files/Bruce_Springsteen_discography.pdf"));
        Document springsteenSongList = loadDocument(toPath("example-files/List_of_songs_recorded_by_Bruce_Springsteen.pdf"));
        ingestor.ingest(springsteenDiscography, springsteenSongList);

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
            URL fileUrl = ChatWithDocumentsBruceSpringsteenQ3WithChatMemory.class.getClassLoader().getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
