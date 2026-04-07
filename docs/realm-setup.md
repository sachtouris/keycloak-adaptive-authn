## Realm Setup for Adaptive Authentication

If you are adding the extension to an **existing realm** (rather than importing the provided `adaptive-realm.json`), you need to configure the following settings.

### 1. Events Configuration

The extension relies on login events to track authentication attempts and compute risk scores.

In the Admin Console, go to **Realm Settings > Events > User events settings**:

- **Save user events** - `ON`
- **Expiration** - set to a reasonable retention period (e.g. `730` days / 2 years)
- **Saved event types** - add at least: `LOGIN`, `LOGIN_ERROR`

Under **Event listeners**, add:

- `login-events-adaptive-authn`

This listener captures login events used by the risk engine for evaluating user behavior.

### 2. Brute Force Protection (workaround for login failure tracking)

> **Important:** Keycloak currently does not provide a way to store and query login failure counts independently. The only mechanism that tracks per-user login failures is the **brute force protection** subsystem. This extension leverages it as a **temporary workaround** to access login failure data for risk evaluation - not primarily for its lockout functionality. This may change in the future if Keycloak introduces a dedicated API for login failure statistics.

In the Admin Console, go to **Realm Settings > Security defenses > Brute force detection**:

- **Enabled** - `ON`
- **Brute force mode** - `Lockout temporarily after multiple failures at different levels` (corresponds to `MULTIPLE` strategy)
- **Max login failures** - `60` (high threshold so the extension evaluates risk before lockout kicks in)
- **Wait increment** - `60` seconds
- **Max wait** - `60` seconds

The high `failureFactor` (60) is intentional - it ensures the adaptive authentication risk engine can assess and react to suspicious login patterns well before the brute force lockout takes effect. Adjust based on your security requirements, but keep in mind the value should be high enough to avoid premature lockouts interfering with the risk evaluation.
