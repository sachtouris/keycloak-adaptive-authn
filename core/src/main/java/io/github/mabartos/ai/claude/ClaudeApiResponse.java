package io.github.mabartos.ai.claude;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response format from Claude API (Anthropic)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ClaudeApiResponse(
        String id,
        String type,
        String role,
        List<ContentBlock> content,
        String model,
        @JsonProperty("stop_reason")
        String stopReason,
        Usage usage
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ContentBlock(String type, String text) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Usage(
            @JsonProperty("input_tokens")
            Integer inputTokens,
            @JsonProperty("output_tokens")
            Integer outputTokens
    ) {
    }

    /**
     * Extract the text content from the response
     */
    public String getText() {
        if (content == null || content.isEmpty()) {
            return "";
        }
        return content.stream()
                .filter(block -> "text".equals(block.type()))
                .map(ContentBlock::text)
                .findFirst()
                .orElse("");
    }
}
