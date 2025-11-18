package edu.coursly.app.service.impl;

import com.google.genai.Client;
import com.google.genai.types.Content;
import edu.coursly.app.service.AIService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GeminiAIServiceImpl implements AIService {

    private final Client geminiClient;

    public GeminiAIServiceImpl(Client geminiClient) {
        this.geminiClient = geminiClient;
    }

    @Override
    public String sendMessage(String message) {

        return geminiClient.models.generateContent("gemini-2.5-flash", message, null).text();
    }

    @Override
    public String sendChatConversation(List<Content> messages) {

        return geminiClient.models.generateContent("gemini-2.5-flash", messages, null).text();
    }
}
