## 2025-05-23 - [Android DataStore Backup Leak]
**Vulnerability:** Jetpack DataStore files (e.g., storing tokens) are included in Android Auto Backup by default, and are stored in plaintext in the internal storage.
**Learning:** `android:allowBackup="true"` is the default. Developers often forget to exclude sensitive files created by libraries like DataStore (`files/datastore/*.json`).
**Prevention:** Always verify `fullBackupContent` or `dataExtractionRules` when storing sensitive data in files, or use EncryptedSharedPreferences/Keystore.

## 2025-05-23 - [Incomplete Backup Exclusion for Secondary Auth Stores]
**Vulnerability:** While `auth_tokens.json` was excluded, `google_user.json` (containing ID Tokens and PII) was not.
**Learning:** When adding new DataStore files (e.g. for Google Auth), developers might forget to update the backup rules XMLs.
**Prevention:** Centralize DataStore file naming or creation to a factory that audits backup rules, or use a single encrypted store for all auth data.

## 2025-05-23 - [AuthPlugin Endpoint Mismatch]
**Vulnerability:** Mismatched endpoint definitions in `AuthPlugin` vs `ApiRoutes` caused `signIn` and `signUp` to be treated as non-public, potentially sending stale tokens to them.
**Learning:** Hardcoding paths in multiple places (interceptor vs API client) leads to drift and logic errors.
**Prevention:** Use single source of truth (constants) for API routes in interceptors.

## 2025-05-23 - [Missing API Timeouts]
**Vulnerability:** HTTP clients lacked timeout configuration, potentially leading to indefinite hanging (DoS risk) if the server is unreachable.
**Learning:** Default Ktor client timeouts are often infinite or very long. Explicit configuration is needed for resilience.
**Prevention:** Install `HttpTimeout` plugin with reasonable values in the central `HttpClientFactory`.
