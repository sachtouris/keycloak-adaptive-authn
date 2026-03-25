# AI Integration

## Selecting the AI Engine

The AI engine provider is configured via the Keycloak SPI option:

```bash
KC_SPI_AI_ENGINE_PROVIDER=claude
```

Available providers: `claude`, `openai`, `granite` (default).

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
