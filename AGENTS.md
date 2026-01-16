# Wytyczne dla Agentów (AGENTS.md)

Poniższe instrukcje muszą być przestrzegane przy każdej modyfikacji kodu.

## Weryfikacja zmian (Pre-commit)

Przed zakończeniem zadania i wysłaniem zmian (submit), **obowiązkowo** uruchom poniższe polecenia, aby zweryfikować poprawność budowania i testów dla platform Android oraz iOS.

### 1. Android
Budowanie wersji Debug oraz uruchomienie testów jednostkowych:
```bash
./gradlew :composeApp:assembleDebug :composeApp:testDebugUnitTest
```

### 2. iOS
Weryfikacja linkowania frameworka oraz uruchomienie testów na symulatorze:
```bash
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64 :composeApp:iosSimulatorArm64Test
```

**Ważne:** Wszystkie powyższe kroki muszą zakończyć się sukcesem (BUILD SUCCESSFUL). W przypadku błędów, należy je naprawić przed wysłaniem zmian.
