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
package io.github.mabartos.ui;

import io.github.mabartos.spi.level.Risk;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.services.ui.extend.UiTabProvider;
import org.keycloak.services.ui.extend.UiTabProviderFactory;
import org.keycloak.utils.StringUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.mabartos.evaluator.client.ClientSensitivityRiskEvaluator.RISK_SENSITIVITY_ATTRIBUTE;

/**
 * Custom UI tab in the client detail page to configure client-specific risk sensitivity.
 */
public class ClientRiskSettingsUiTab implements UiTabProvider, UiTabProviderFactory<ComponentModel> {
    private static final Logger logger = Logger.getLogger(ClientRiskSettingsUiTab.class);

    private static final String SENSITIVITY_CONFIG = "riskSensitivity";

    private static final List<String> SENSITIVITY_OPTIONS = Arrays.stream(Risk.Score.values())
            .filter(s -> s != Risk.Score.INVALID)
            .map(Enum::name)
            .toList();

    @Override
    public String getId() {
        return "Risk-based settings";
    }

    @Override
    public String getPath() {
        return "/:realm/clients/:clientId/:tab";
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("tab", "risk-sensitivity");
        return params;
    }

    @Override
    public String getHelpText() {
        return "Configure risk sensitivity for this client";
    }

    @Override
    public void onCreate(KeycloakSession session, RealmModel realm, ComponentModel model) {
        logger.debugf("onCreate execution");
    }

    @Override
    public void onUpdate(KeycloakSession session, RealmModel realm, ComponentModel oldModel, ComponentModel newModel) {
        logger.debugf("onUpdate execution");

        String clientId = newModel.get("clientId");
        if (StringUtil.isBlank(clientId)) {
            logger.warnf("onUpdate: no clientId in component model");
            return;
        }

        ClientModel client = session.clients().getClientById(realm, clientId);
        if (client == null) {
            logger.warnf("onUpdate: client '%s' not found", clientId);
            return;
        }

        String sensitivity = newModel.get(SENSITIVITY_CONFIG);
        if (StringUtil.isNotBlank(sensitivity)) {
            logger.debugf("onUpdate: setting '%s' = '%s' on client '%s'", RISK_SENSITIVITY_ATTRIBUTE, sensitivity, client.getClientId());
            client.setAttribute(RISK_SENSITIVITY_ATTRIBUTE, sensitivity);
        } else {
            logger.debugf("onUpdate: removing '%s' from client '%s'", RISK_SENSITIVITY_ATTRIBUTE, client.getClientId());
            client.removeAttribute(RISK_SENSITIVITY_ATTRIBUTE);
        }
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel model) throws ComponentValidationException {
        logger.debugf("validateConfiguration execution");

        String sensitivity = model.get(SENSITIVITY_CONFIG);
        if (StringUtil.isNotBlank(sensitivity)) {
            try {
                Risk.Score.valueOf(sensitivity);
            } catch (IllegalArgumentException e) {
                throw new ComponentValidationException("Invalid risk sensitivity value: " + sensitivity);
            }
        }

        // Load current client's attribute value into the model so the form reflects it on next load
        String clientId = model.get("clientId");
        if (StringUtil.isNotBlank(clientId)) {
            ClientModel client = session.clients().getClientById(realm, clientId);
            if (client != null) {
                String current = client.getAttribute(RISK_SENSITIVITY_ATTRIBUTE);
                if (StringUtil.isNotBlank(current)) {
                    model.getConfig().putSingle(SENSITIVITY_CONFIG, current);
                }
            }
        }
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property()
                .name(SENSITIVITY_CONFIG)
                .label("Risk sensitivity")
                .helpText("Risk sensitivity level for this client. Higher values mean stricter authentication requirements.")
                .type(ProviderConfigProperty.LIST_TYPE)
                .options(SENSITIVITY_OPTIONS)
                .defaultValue(Risk.Score.NONE.name())
                .add()
                .build();
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
}
