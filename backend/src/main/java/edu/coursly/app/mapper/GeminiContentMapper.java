package edu.coursly.app.mapper;

import com.google.genai.types.Content;
import com.google.genai.types.Part;
import edu.coursly.app.model.dto.ChatContent;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GeminiContentMapper {

    public Content toGeminiContent(ChatContent chatContent) {

        List<Part> parts =
                chatContent.parts().stream()
                        .map(textPart -> Part.fromText(textPart.text()))
                        .toList();

        return Content.builder().role(chatContent.role()).parts(parts).build();
    }
}
