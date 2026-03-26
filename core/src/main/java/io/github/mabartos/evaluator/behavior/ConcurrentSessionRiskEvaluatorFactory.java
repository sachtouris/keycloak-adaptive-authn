package io.github.mabartos.evaluator.behavior;

import io.github.mabartos.spi.evaluator.RiskEvaluator;
import io.github.mabartos.spi.evaluator.RiskEvaluatorFactory;
import org.keycloak.models.KeycloakSession;

public class ConcurrentSessionRiskEvaluatorFactory implements RiskEvaluatorFactory {
    public static final String PROVIDER_ID = "concurrent-session-continuous";
    protected static final String NAME = "Concurrent session continuous evaluator";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Class<? extends RiskEvaluator> evaluatorClass() {
        return ConcurrentSessionRiskEvaluator.class;
    }

    @Override
    public ConcurrentSessionRiskEvaluator create(KeycloakSession session) {
        return new ConcurrentSessionRiskEvaluator(session);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
