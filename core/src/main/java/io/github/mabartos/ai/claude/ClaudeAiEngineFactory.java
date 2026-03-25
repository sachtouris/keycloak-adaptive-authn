package io.github.mabartos.ai.claude;

import io.github.mabartos.spi.ai.AiEngineFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.quarkus.runtime.configuration.Configuration;

import java.util.Optional;

public class ClaudeAiEngineFactory implements AiEngineFactory {
    public static final String PROVIDER_ID = "claude";

    private static final String URL_PROPERTY = "ai.claude.api.url";
    private static final String KEY_PROPERTY = "ai.claude.api.key";
    private static final String MODEL_PROPERTY = "ai.claude.api.model";
    private static final String VERSION_PROPERTY = "ai.claude.api.version";
    private static final String ENABLE_CACHING_PROPERTY = "ai.claude.api.enable.caching";
    private static final String DEFAULT_VERSION = "2023-06-01";

    @Override
    public ClaudeAiEngine create(KeycloakSession session) {
        return new ClaudeAiEngine(session);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    public static Optional<String> getApiUrl() {
        return Configuration.getOptionalValue(URL_PROPERTY);
    }

    public static Optional<String> getApiKey() {
        return Configuration.getOptionalValue(KEY_PROPERTY);
    }

    public static Optional<String> getModel() {
        return Configuration.getOptionalValue(MODEL_PROPERTY);
    }

    public static String getApiVersion() {
        return Configuration.getOptionalValue(VERSION_PROPERTY).orElse(DEFAULT_VERSION);
    }

    public static boolean isCachingEnabled() {
        return Configuration.getOptionalValue(ENABLE_CACHING_PROPERTY)
                .map(Boolean::parseBoolean)
                .orElse(true);  // Enable by default for cost savings
    }
}
