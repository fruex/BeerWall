## 2026-01-19 - Hardcoded API Keys & Logging Security
**Vulnerability:** Found sensitive API keys (PayNow sandbox) stored in plain text in `http/http-client.env.json` and `LogLevel.ALL` in `HttpClientFactory` which logs request bodies including passwords.
**Learning:** Developers often use local environment files for convenience with HTTP clients and forget to exclude them from git. Verbose logging in debug builds can leak sensitive data if not restricted.
**Prevention:** Added `http-client.env.json` to `.gitignore`, created a template `http-client.env.json.example`, and restricted logging to `LogLevel.HEADERS`.
