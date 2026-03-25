package io.github.mabartos.ai.claude;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.mabartos.ai.DefaultAiDataRequest;

import java.util.List;

/**
 * Request format for Claude API (Anthropic)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClaudeApiRequest(
        String model,
        @JsonProperty("max_tokens")
        Integer maxTokens,
        List<Message> messages,
        Object system,  // Can be String or List<SystemBlock>
        Double temperature,
        @JsonProperty("output_config")
        OutputConfig outputConfig
) {

    public record Message(String role, String content) {
    }

    public record SystemBlock(
            String type,
            String text,
            @JsonProperty("cache_control")
            CacheControl cacheControl
    ) {
    }

    public record CacheControl(String type) {
    }

    public record OutputConfig(Format format) {
        public record Format(String type, Schema schema) {
            public record Schema(String type,
                                 Boolean additionalProperties,
                                 java.util.Map<String, Property> properties,
                                 List<String> required) {
                public record Property(String type, String description) {
                }
            }
        }
    }

    public static ClaudeApiRequest create(String model, String systemMessage, String userMessage, DefaultAiDataRequest.ResponseFormat responseFormat, boolean enableCaching) {
        List<Message> messages = List.of(new Message("user", userMessage));

        OutputConfig outputConfig = null;
        if (responseFormat != null && responseFormat.jsonSchema() != null) {
            // Convert DefaultAiDataRequest schema to Claude schema format
            var properties = responseFormat.jsonSchema().schema().properties();
            java.util.Map<String, OutputConfig.Format.Schema.Property> claudeSchema = properties.entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                            java.util.Map.Entry::getKey,
                            e -> new OutputConfig.Format.Schema.Property(e.getValue().type(), e.getValue().description())
                    ));

            var schema = new OutputConfig.Format.Schema(
                "object",
                false,
                claudeSchema,
                List.copyOf(claudeSchema.keySet())
            );
            outputConfig = new OutputConfig(
                new OutputConfig.Format("json_schema", schema)
            );
        }

        // Use structured system blocks with caching if enabled
        Object systemParameter = enableCaching
                ? List.of(new SystemBlock("text", systemMessage, new CacheControl("ephemeral")))
                : systemMessage;

        return new ClaudeApiRequest(
            model,
            1024,
            messages,
            systemParameter,
            0.2,
            outputConfig
        );
    }
}
