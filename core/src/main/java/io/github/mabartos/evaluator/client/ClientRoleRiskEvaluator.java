package io.github.mabartos.evaluator.client;

import io.github.mabartos.spi.evaluator.AbstractRiskEvaluator;
import io.github.mabartos.spi.level.Risk;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.keycloak.models.AdminRoles;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;

import java.util.Map;
import java.util.Set;

/**
 * Risk evaluator based on the user's client roles for the target client.
 * <p>
 * Uses Keycloak's role naming convention to classify risk by prefix.
 * <p>
 * Users with no client roles on the target client receive a trust signal ({@link Risk.Score#NEGATIVE_LOW}).
 */
public class ClientRoleRiskEvaluator extends AbstractRiskEvaluator {

    private static final String MANAGE_PREFIX = "manage-";
    private static final String CREATE_PREFIX = "create-";
    private static final String VIEW_PREFIX = "view-";
    private static final String QUERY_PREFIX = "query-";

    private static final Set<String> SENSITIVE_ROLES = Set.of(AdminRoles.IMPERSONATION);

    private final KeycloakSession session;

    public ClientRoleRiskEvaluator(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Set<EvaluationPhase> evaluationPhases() {
        return Set.of(EvaluationPhase.USER_KNOWN);
    }

    @Override
    public Risk evaluate(@Nonnull RealmModel realm, @Nullable UserModel knownUser) {
        if (knownUser == null) {
            return Risk.invalid("User is null");
        }

        var authSession = session.getContext().getAuthenticationSession();
        if (authSession == null) {
            return Risk.invalid("No authentication session available");
        }

        ClientModel client = authSession.getClient();
        if (client == null) {
            return Risk.invalid("No client in authentication session");
        }

        String clientId = client.getClientId();
        Risk highest = Risk.of(Risk.Score.NEGATIVE_LOW, "User has no sensitive client roles on '%s'".formatted(clientId));

        var roles = knownUser.getClientRoleMappingsStream(client).toList();

        for (RoleModel role : roles) {
            Risk.Score score = scoreForRole(role.getName());
            Risk current = Risk.of(score, "Client role '%s' on '%s'".formatted(role.getName(), clientId));
            highest = highest.max(current);
        }

        return highest;
    }

    private Risk.Score scoreForRole(String roleName) {
        if (SENSITIVE_ROLES.contains(roleName)) return Risk.Score.MEDIUM;
        if (roleName.startsWith(MANAGE_PREFIX)) return Risk.Score.MEDIUM;
        if (roleName.startsWith(CREATE_PREFIX)) return Risk.Score.SMALL;
        if (roleName.startsWith(VIEW_PREFIX)) return Risk.Score.NONE;
        if (roleName.startsWith(QUERY_PREFIX)) return Risk.Score.NONE;
        return Risk.Score.NONE;
    }
}
