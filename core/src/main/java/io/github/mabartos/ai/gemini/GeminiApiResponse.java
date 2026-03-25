package io.github.mabartos.ai.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response format from Google Gemini API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GeminiApiResponse(
        List<Candidate> candidates,
        @JsonProperty("usageMetadata")
        UsageMetadata usageMetadata
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Candidate(
            Content content,
            @JsonProperty("finishReason")
            String finishReason
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Content(List<Part> parts) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Part(String text) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UsageMetadata(
            @JsonProperty("promptTokenCount")
            Integer promptTokenCount,
            @JsonProperty("candidatesTokenCount")
            Integer candidatesTokenCount,
            @JsonProperty("totalTokenCount")
            Integer totalTokenCount
    ) {
    }

    /**
     * Extract the text content from the first candidate
     */
    public String getText() {
        if (candidates == null || candidates.isEmpty()) {
            return "";
        }

        return candidates.getFirst().content().parts().stream()
                .map(Part::text)
                .findFirst()
                .orElse("");
    }
}
