package io.github.mabartos.spi.engine;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ConfiguredProvider;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderFactory;

import java.util.Collections;
import java.util.List;

public interface RiskScoreAlgorithmFactory extends ProviderFactory<RiskScoreAlgorithm>, ConfiguredProvider {

    /**
     * Get name of the algorithm
     */
    String getName();

    /**
     * Get description of the algorithm representing details about risk score calculation
     */
    String getDescription();

    @Override
    default String getHelpText() {
        return getDescription();
    }

    @Override
    default List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
    }

    @Override
    default void init(Config.Scope scope) {
        //noop
    }

    @Override
    default void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        //noop
    }

    @Override
    default void close() {
        //noop
    }
}
