package io.github.mabartos.ai.gemini;

import io.github.mabartos.spi.ai.AiEngineFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.quarkus.runtime.configuration.Configuration;

import java.util.Optional;

public class GeminiAiEngineFactory implements AiEngineFactory {
    public static final String PROVIDER_ID = "gemini";

    private static final String URL_PROPERTY = "ai.gemini.api.url";
    private static final String KEY_PROPERTY = "ai.gemini.api.key";
    private static final String MODEL_PROPERTY = "ai.gemini.api.model";

    @Override
    public GeminiAiEngine create(KeycloakSession session) {
        return new GeminiAiEngine(session);
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
}
