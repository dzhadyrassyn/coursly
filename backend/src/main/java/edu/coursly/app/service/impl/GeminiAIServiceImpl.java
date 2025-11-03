package edu.coursly.app.service.impl;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import edu.coursly.app.service.AIService;
import org.springframework.stereotype.Service;

@Service
public class GeminiAIServiceImpl implements AIService {

    private final Client geminiClient;

    public GeminiAIServiceImpl(Client geminiClient) {
        this.geminiClient = geminiClient;
    }

    @Override
    public String sendMessage(String message) {

        GenerateContentResponse response =
                geminiClient.models.generateContent(
                        "gemini-2.5-flash", message, null);

        return response.text();
    }
}
