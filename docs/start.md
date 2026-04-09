## Getting started

### Building from Source

To build it from source, execute this command:

```shell
./mvnw clean install -DskipTests
```

If you want to try it out, follow this:

1. Build it with profile `-Pbuild-distribution` as:

```shell
./mvnw -f core clean install -DskipTests -Pbuild-distribution
```
2. Prepare your `.env` file with necessary configuration (see `.env.example` for more info)

3. Start the server with deployed extension

```shell
./mvnw exec:exec@start-server
```

4. Then you can access [User account](http://localhost:8080/realms/adaptive/account) to see the functionality in action.

### Building with Different Keycloak Versions

The project uses **Keycloak nightly builds (999.0.0-SNAPSHOT) by default**. All dependencies and the distribution are fetched from GitHub releases. You can also build with any specific released version.

#### Use Nightly Build (Default)
```shell
./mvnw -f core clean install -DskipTests -Pbuild-distribution
```
- Uses Keycloak version: `999.0.0-SNAPSHOT` for all dependencies
- Downloads distribution from: `https://github.com/keycloak/keycloak/releases/download/nightly/keycloak-999.0.0-SNAPSHOT.zip`

#### Use Specific Release

To build with a specific Keycloak release, set both the version and the GitHub tag:

Example for Keycloak 26.5.2:
```shell
./mvnw -f core clean install -DskipTests -Pbuild-distribution \
  -Dkeycloak.version=26.5.2 \
  -Dkeycloak.github.tag=26.5.2
```
- Uses Keycloak version: `26.5.2` for all dependencies
- Downloads distribution from: `https://github.com/keycloak/keycloak/releases/download/26.5.2/keycloak-26.5.2.zip`

Example for Keycloak 26.5.4:
```shell
./mvnw -f core clean install -DskipTests -Pbuild-distribution \
  -Dkeycloak.version=26.5.4 \
  -Dkeycloak.github.tag=26.5.4
```

**Key Points:**
- **Nightly is default** - Both Maven dependencies and the distribution use `999.0.0-SNAPSHOT` from the `nightly` tag
- **For specific versions** - Set both `-Dkeycloak.version` and `-Dkeycloak.github.tag` to the same version
- **Downloads are cached** in `~/.m2/repository/.cache/keycloak/{tag}/` for faster rebuilds
- **All distributions come from GitHub** - no Maven Central dependency

**Clear Download Cache:**

If you need to re-download a fresh copy:
```shell
rm -rf ~/.m2/repository/.cache/keycloak/
```

### Container

You can build your own containerized Keycloak installation with this extension as described in this
guide: [Add Keycloak Adaptive Authentication extension](https://github.com/mabartos/keycloak-quarkus-extensions/blob/main/examples/keycloak-adaptive-authn.md).

**NOTE**: This is an old release with the authentication policies that are not part of this repository anymore.
Recommended way is to build it from source for now or follow steps on the guide mentioned above.

You can use the container image by running:

    podman run -p 8080:8080 quay.io/mabartos/keycloak-adaptive-all start

This command starts Keycloak exposed on the local port 8080 (`localhost:8080`).

In order to see the functionality in action, navigate to `localhost:8080/realms/authn-policy-adaptive/account`.

ℹ️ **INFO:** If you want to use the OpenAI capabilities, set the environment variables (by setting `-e OPEN_AI_API_*`)
for the image described in the [README](adaptive/README.md#integration-with-openai) of the `adaptive` module..

ℹ️ **INFO:** If you have installed Docker, use `docker` instead of `podman`.

## Realm Setup

If you want to use the extension on your **own existing realm** instead of the provided example realm, see the [Realm Setup Guide](realm-setup.md) for the required Events and Brute Force configuration.

## Show example flow

In order to see the execution of the authentication flow from the example realm `adaptive`, just access the url
`http://localhost:8080/admin/adaptive/console/`.

## AI Engine Integration

For configuring AI engines (OpenAI, Claude, Gemini, IBM Granite), see the [AI Engine Integration Guide](ai-engine-integration.md).