# Checklist Deploymentu do Google Play - BeerWall (IgiBeer)

## 🔐 1. Konfiguracja Podpisywania Aplikacji

- [ ] **Utworzenie release keystore**
  - Wygenerować keystore dla produkcji: `keytool -genkey -v -keystore release.keystore -alias beerwall -keyalg RSA -keysize 2048 -validity 10000`
  - Przechowywać keystore w bezpiecznym miejscu (NIGDY nie commitować do repo)

- [ ] **Konfiguracja signing w `build.gradle.kts`**
  - Dodać signingConfigs dla release
  - Skonfigurować zmienne środowiskowe lub `keystore.properties` dla danych keystore
  ```kotlin
  android {
      signingConfigs {
          create("release") {
              storeFile = file(keystoreProperties["storeFile"] as String)
              storePassword = keystoreProperties["storePassword"] as String
              keyAlias = keystoreProperties["keyAlias"] as String
              keyPassword = keystoreProperties["keyPassword"] as String
          }
      }
      buildTypes {
          release {
              signingConfig = signingConfigs.getByName("release")
          }
      }
  }
  ```

## 📱 2. Manifest i Permissions

- [x] **Network Security Configuration**
  - ✅ Skonfigurowano `network_security_config.xml`
  - ✅ Cleartext traffic dozwolony tylko dla debug API (api-debug.igibeer.pl, localhost)
  - ✅ Release wymusza HTTPS (cleartextTrafficPermitted=false)

- [ ] **Weryfikacja permissions**
  - ✅ NFC permission (opcjonalny)
  - ✅ INTERNET permission
  - Sprawdzić czy nie ma niepotrzebnych uprawnień

- [ ] **Dodać backup rules** (opcjonalne)
  - Skonfigurować `android:fullBackupContent` dla kontroli nad backupem użytkownika

## 🎨 3. Grafika i Branding

- [ ] **Ikona aplikacji**
  - ✅ Ikony są obecne w `res/mipmap-*`
  - Zweryfikować czy ikona wygląda profesjonalnie
  - Przygotować feature graphic (1024x500)
  - Przygotować screenshoty (min. 2, zalecane 8) dla różnych rozmiarów ekranów

- [ ] **Adaptive icon**
  - Sprawdzić czy ikona dobrze wygląda w różnych kształtach (koło, kwadrat, zaokrąglony kwadrat)

## 📋 4. Store Listing

- [ ] **Teksty marketingowe**
  - Krótki opis (max 80 znaków)
  - Pełny opis (max 4000 znaków)
  - Tłumaczenia (minimum PL + EN)

- [ ] **Grafiki promocyjne**
  - Feature graphic (1024x500)
  - Screenshots (minimum 2 na platformę)
  - Opcjonalnie: Promotional video

- [ ] **Kategoria aplikacji**
  - Wybrać odpowiednią kategorię (prawdopodobnie: Tools lub Lifestyle)

- [ ] **Content rating**
  - Wypełnić kwestionariusz ratingu treści w Google Play Console

## 🔒 5. Polityki i Zgodność z RODO

- [ ] **Privacy Policy**
  - ✅ Polityka prywatności została utworzona
  - Hostowana na publicznie dostępnym URL
  - **TODO:** Dodać URL polityki w Google Play Console podczas publikacji
  - **OPCJONALNIE:** Dodać link do polityki w aplikacji (np. ekran ustawień/logowania)

- [ ] **Terms of Service** (opcjonalne ale zalecane)
  - Stworzyć regulamin korzystania z aplikacji

- [ ] **Zgodność z RODO**
  - Aplikacja zbiera dane użytkownika (tokeny, karty NFC, transakcje)
  - Dodać informacje o przetwarzaniu danych
  - Zapewnić możliwość usunięcia konta

- [ ] **Data Safety Form**
  - Wypełnić formularz Data Safety w Google Play Console
  - Wskazać jakie dane są zbierane, przechowywane i udostępniane

## 🏗️ 6. Build Configuration

- [ ] **Wersjonowanie**
  - ✅ `versionCode = 1` (OK dla pierwszego release)
  - ✅ `versionName = "1.0"` (OK)
  - Zaplanować schemat wersjonowania dla przyszłych wydań

- [ ] **ProGuard/R8**
  - ✅ ProGuard skonfigurowany
  - Przetestować release build czy aplikacja działa poprawnie z obfuscation
  - Dodać reguły dla wszystkich używanych bibliotek (obecnie są podstawowe)

- [ ] **Test release build lokalnie**
  - `./gradlew :composeApp:assembleRelease`
  - Zainstalować i przetestować APK na prawdziwym urządzeniu
  - Sprawdzić rozmiar APK (optymalizacja)

## 🧪 7. Testowanie

- [ ] **Testy funkcjonalne**
  - Logowanie (Google Sign-In)
  - Skanowanie kart NFC
  - Doładowanie środków
  - Historia transakcji
  - Pull-to-refresh
  - Obsługa błędów sieciowych

- [ ] **Testy na różnych urządzeniach**
  - Minimum Android 8.1 (API 27 - zgodnie z minSdk)
  - Różne rozmiary ekranów
  - Urządzenia z i bez NFC

- [ ] **Testy crashowania**
  - Brak crashy w critical paths
  - Obsługa edge cases

- [ ] **Internal/Closed testing**
  - Utworzyć closed track w Google Play Console
  - Zaprosić testerów beta
  - Zebrać feedback przed public release

## 🔍 8. Code Quality i TODO

- [ ] **Rozwiązać TODO w kodzie** (znalezionych: 10)
  - `TokenManager.kt:46` - Refactoring architektury (Clean Architecture)
  - `TokenManager.ios.kt` - iOS Keychain implementation (4 instancje)
  - `GoogleAuth.ios.kt` - iOS Google Sign-In implementation (3 instancje)
  - `AppNavHost.kt:35` - Refactoring state management
  - `AuthRepository.kt:5` - Refactoring modelu tokenów
  - `Transaction.kt:9` - Użycie `kotlinx-datetime`

- [ ] **iOS Support** (opcjonalne dla pierwszej wersji)
  - ⚠️ iOS implementacja Google Auth jest placeholder
  - ⚠️ iOS Keychain nie jest zaimplementowany
  - Rozważyć czy wypuszczać wersję iOS jednocześnie

## 🌐 9. API i Backend

- [x] **Weryfikacja BASE_URL**
  - Debug: `http://api-debug.igibeer.pl:7000`
  - Release: `https://api.igibeer.pl`
  - ✅ Skonfigurowano produkcyjny endpoint
  - Upewnić się że backend produkcyjny jest stabilny i gotowy

- [ ] **Obsługa błędów API**
  - Sprawdzić czy wszystkie błędy sieciowe są prawidłowo obsługiwane
  - User-friendly komunikaty błędów

- [ ] **Rate limiting**
  - Sprawdzić czy backend ma rate limiting
  - Dodać retry logic w kliencie

## 📊 10. Analytics i Monitoring

- [ ] **Crash Reporting**
  - Rozważyć dodanie Firebase Crashlytics lub podobnego
  - Monitoring błędów w produkcji

- [ ] **Analytics** (opcjonalne)
  - Firebase Analytics lub alternatywa
  - Tracking kluczowych eventów (login, NFC scan, payment)

## 🚀 11. Google Play Console Setup

- [ ] **Utworzenie konta deweloperskiego**
  - Opłata jednorazowa $25
  - Weryfikacja tożsamości

- [ ] **Utworzenie aplikacji**
  - Wybrać nazwę aplikacji (aktualnie: "IgiBeer")
  - Ustawić domyślny język

- [ ] **Release Management**
  - Najpierw: Internal testing
  - Potem: Closed testing (beta)
  - Na końcu: Production

- [ ] **App Bundle vs APK**
  - Zalecane: Android App Bundle (`.aab`)
  - Zmienić build na `bundleRelease` zamiast `assembleRelease`

## 🔔 12. Pre-launch Checklist

- [ ] **Przygotować Release Notes**
  - Opisać funkcje aplikacji
  - Przygotować w PL i EN

- [ ] **Support i kontakt**
  - Email kontaktowy (wymagany przez Google Play)
  - Strona www (opcjonalna)

- [ ] **Plan marketingowy**
  - Przygotować kampanię promocyjną
  - Media społecznościowe

## 📝 Notatki Dodatkowe

### Aktualna konfiguracja:
- **Package name:** `com.fruex.beerwall`
- **App name:** IgiBeer
- **Min SDK:** 27 (Android 8.1)
- **Target SDK:** 36 (Android 14)
- **Version:** 1.0 (versionCode: 1)

### Infrastruktura:
- Kotlin Multiplatform (Android + iOS)
- Jetpack Compose
- Ktor client
- Google Sign-In
- NFC support
- DataStore (persistent storage)

### Zależności zewnętrzne:
- Google OAuth (wymagany client_id: obecnie w strings.xml)
- Backend API (Azure)
- NFC hardware (opcjonalne)

---

## ✅ Status Gotowości: ~75%

**Krytyczne blokery:**
1. ❌ Brak release keystore i konfiguracji signing
2. ⚠️ Brak testowania release buildu z produkcyjnym API

**Zalecane przed pierwszym release:**
- Rozwiązanie TODO w kodzie
- Testy na prawdziwych urządzeniach
- Beta testing program
- Crash reporting/monitoring
