package io.github.mabartos.spi.engine;

import io.github.mabartos.spi.evaluator.RiskEvaluator;
import jakarta.annotation.Nonnull;

public class StoredRiskProperties {
    public static final String PREFIX = "adaptive.";

    public static String getDataPrefix(RiskEvaluator.EvaluationPhase phase) {
        return "%s.%s.".formatted(PREFIX, phase.name());
    }

    public static String getDataPrefix(RiskEvaluator.EvaluationPhase phase, Class<? extends RiskEvaluator> riskEvaluatorClass) {
        return "%s.%s.".formatted(getDataPrefix(phase), riskEvaluatorClass.getSimpleName().toLowerCase());
    }

    public static String getOverallPrefix(@Nonnull RiskEvaluator.EvaluationPhase phase) {
        return getDataPrefix(phase) + "overall.";
    }

    public static String getOverallPrefix() {
        return "%s.overall".formatted(PREFIX);
    }

    public static String getAlgorithmPrefix(RiskEvaluator.EvaluationPhase phase) {
        return getDataPrefix(phase) + "algorithm.";
    }

    public static String getAlgorithmId(RiskEvaluator.EvaluationPhase phase) {
        return getAlgorithmPrefix(phase) + "id";
    }

    public static String getOverallScoreProperty(@Nonnull RiskEvaluator.EvaluationPhase phase) {
        return getOverallPrefix(phase) + "score";
    }

    public static String getOverallScoreProperty() {
        return getOverallPrefix() + "score";
    }

    public static String getOverallSummaryProperty(@Nonnull RiskEvaluator.EvaluationPhase phase) {
        return getOverallPrefix(phase) + "summary";
    }

    public static String getOverallSummaryProperty() {
        return getOverallPrefix() + "summary";
    }
}
