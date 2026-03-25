package io.github.mabartos.ai.gemini;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.mabartos.ai.DefaultAiDataRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Request format for Google Gemini API
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeminiApiRequest(
        List<Content> contents,
        @JsonProperty("systemInstruction")
        SystemInstruction systemInstruction,
        @JsonProperty("generationConfig")
        GenerationConfig generationConfig
) {

    public record Content(List<Part> parts) {
    }

    public record Part(String text) {
    }

    public record SystemInstruction(List<Part> parts) {
    }

    public record GenerationConfig(
            Double temperature,
            @JsonProperty("responseMimeType")
            String responseMimeType,
            @JsonProperty("responseSchema")
            ResponseSchema responseSchema
    ) {
        public record ResponseSchema(
                String type,
                Map<String, Property> properties,
                List<String> required
        ) {
            public record Property(String type, String description) {
            }
        }
    }

    public static GeminiApiRequest create(String systemMessage, String userMessage, DefaultAiDataRequest.ResponseFormat responseFormat) {
        SystemInstruction systemInst = null;
        if (systemMessage != null && !systemMessage.isEmpty()) {
            systemInst = new SystemInstruction(List.of(new Part(systemMessage)));
        }

        Content content = new Content(List.of(new Part(userMessage)));

        GenerationConfig genConfig;
        if (responseFormat != null && responseFormat.jsonSchema() != null) {
            // Convert DefaultAiDataRequest schema to Gemini schema format
            var properties = responseFormat.jsonSchema().schema().properties();
            Map<String, GenerationConfig.ResponseSchema.Property> geminiSchema = properties.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> new GenerationConfig.ResponseSchema.Property(e.getValue().type(), e.getValue().description())
                    ));

            var responseSchema = new GenerationConfig.ResponseSchema(
                    "object",
                    geminiSchema,
                    List.copyOf(geminiSchema.keySet())
            );
            genConfig = new GenerationConfig(0.2, "application/json", responseSchema);
        } else {
            genConfig = new GenerationConfig(0.2, null, null);
        }

        return new GeminiApiRequest(List.of(content), systemInst, genConfig);
    }
}
