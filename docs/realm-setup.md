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
