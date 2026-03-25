# AI Engine Integration

## Selecting the AI Engine

The AI engine provider is configured via the Keycloak SPI option:

```bash
KC_SPI_AI_ENGINE_PROVIDER=claude
```

Available providers: `claude`, `gemini`, `openai`, `granite` (default).

## Claude (Anthropic)

### Getting an API Key

1. Visit [console.anthropic.com](https://console.anthropic.com/)
2. Sign up or log in
3. Navigate to **API Keys** and generate a new key (starts with `sk-ant-`)

### Configuration

| Environment Variable | Required | Default |
|---|---|---|
| `CLAUDE_API_KEY` | Yes | - |
| `CLAUDE_API_URL` | No | `https://api.anthropic.com/v1/messages` |
| `CLAUDE_API_MODEL` | No | `claude-haiku-4-5-20251001` |
| `CLAUDE_API_VERSION` | No | `2023-06-01` |
| `CLAUDE_API_ENABLE_CACHING` | No | `true` |

## Google Gemini

### Getting an API Key

1. Visit [aistudio.google.com/app/apikey](https://aistudio.google.com/app/apikey)
2. Sign in with your Google account
3. Click **Get API Key** and generate a new key (starts with `AIzaSy...`)

A free tier is available (15 requests/minute for Flash).

### Configuration

| Environment Variable | Required | Default |
|---|---|---|
| `GEMINI_API_KEY` | Yes | - |
| `GEMINI_API_URL` | No | `https://generativelanguage.googleapis.com/v1beta` |
| `GEMINI_API_MODEL` | No | `gemini-2.5-flash-lite` |
