package edu.coursly.app.config;

import com.google.genai.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiClientConfig {

    @Bean
    public Client geminiClient() {
        return new Client(); // The client gets the API key from the environment variable
        // `GOOGLE_API_KEY`
    }
}
