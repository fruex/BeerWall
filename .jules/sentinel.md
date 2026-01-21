## 2025-05-23 - [Android DataStore Backup Leak]
**Vulnerability:** Jetpack DataStore files (e.g., storing tokens) are included in Android Auto Backup by default, and are stored in plaintext in the internal storage.
**Learning:** `android:allowBackup="true"` is the default. Developers often forget to exclude sensitive files created by libraries like DataStore (`files/datastore/*.json`).
**Prevention:** Always verify `fullBackupContent` or `dataExtractionRules` when storing sensitive data in files, or use EncryptedSharedPreferences/Keystore.
