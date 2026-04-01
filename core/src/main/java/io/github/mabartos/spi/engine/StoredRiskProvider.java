/*
 * Copyright 2024 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.mabartos.spi.engine;

import io.github.mabartos.spi.level.ResultRisk;
import io.github.mabartos.spi.level.Risk;
import io.github.mabartos.spi.evaluator.RiskEvaluator;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.keycloak.provider.Provider;

import java.util.Map;
import java.util.Optional;

/**
 * Provider for storing the evaluated overall risk score through the authentication flow processing
 * The overall risk score is calculated in several phases and then aggregated
 */
public interface StoredRiskProvider extends Provider {

    /**
     * Get evaluated overall stored risk score
     *
     * @return overall risk score in range (0,1>
     */
    ResultRisk getStoredOverallRisk();

    /**
     * Get evaluated stored risk score for the specific phase
     *
     * @param phase phase of the evaluation
     * @return overall risk score in range (0,1>
     */
    ResultRisk getStoredRisk(@Nonnull RiskEvaluator.EvaluationPhase phase);

    /**
     * Store the overall risk score
     *
     * @param risk overall risk score in range (0,1> and other attributes
     */
    void storeOverallRisk(@Nonnull ResultRisk risk);

    /**
     * Store the overall risk score for the specific phase
     *
     * @param risk      risk score in range (0,1> and other attributes
     * @param phase     phase of the risk score evaluation
     */
    void storeRisk(@Nonnull ResultRisk risk, @Nonnull RiskEvaluator.EvaluationPhase phase);

    /**
     * Store additional data associated with the risk evaluation.
     * Can be used by algorithms to persist algorithm-specific metadata (e.g., evidence scores).
     *
     * @param key   the property key
     * @param value the property value
     */
    void storeAdditionalData(String key, String value);

    /**
     * Get additional data associated with the risk evaluation.
     *
     * @param key the property key
     * @return the stored value, or empty if not present
     */
    Optional<String> getAdditionalData(String key);
}