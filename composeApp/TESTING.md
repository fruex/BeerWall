# Strategia Testowania UI w BeerWall (KMP)

## Podsumowanie zmian
Twoje poprzednie testy (`LoginUiTest`) znajdowały się w katalogu `commonTest`, ale używały biblioteki **Robolectric**, która działa tylko na Androidzie (JVM). Powodowało to błędy kompilacji na iOS oraz problemy z ładowaniem zasobów (Compose Resources) w środowisku lokalnym.

Zdecydowałem się przenieść ten test do `androidInstrumentedTest` (testy instrumentowane), co jest **rekomendowanym podejściem** dla testów UI wymagających pełnej wierności (zasoby, grafika, cykl życia).

## Odpowiedź na Twoje pytanie
> "Czy testy ui lepiej robić tak jak mam czy np. z expresso?"

**Odpowiedź: Lepiej robić to jako Testy Instrumentowane (podobnie jak Espresso).**

### Dlaczego?
1.  **Compose Multiplatform Resources:** Biblioteka zasobów KMP w wersji 1.6+ wymaga pełnego kontekstu Androida do poprawnego działania. Robolectric (testy lokalne) często ma problemy z inicjalizacją tego kontekstu, co powoduje błędy typu `Android context is not initialized`.
2.  **Wierność:** Testy instrumentowane (uruchamiane na emulatorze lub urządzeniu) dają 100% pewności, że UI wygląda i działa tak, jak u użytkownika.
3.  **Przyszłość iOS:** Chociaż test znajduje się teraz w katalogu Androida, używa on API `runComposeUiTest`. W przyszłości, aby testować na iOS, możesz:
    *   Wydzielić logikę testu (interakcje) do wspólnej funkcji w `commonTest`.
    *   Uruchamiać ją z poziomu `androidInstrumentedTest` (na Androidzie) oraz `iosTest` (na iOS - używając XCTest).

## Jak uruchamiać testy?

### Testy UI (Instrumentowane)
Wymagają podłączonego urządzenia lub emulatora.
```bash
./gradlew :composeApp:connectedDebugAndroidTest
```
Lub kliknij zieloną strzałkę obok testu w Android Studio (wybierając konfigurację Android Instrumented Tests).

### Testy Jednostkowe (Unit Tests)
Szybkie testy logiki (ViewModel, UseCase), niewymagające UI.
```bash
./gradlew :composeApp:testDebugUnitTest
```

## Struktura katalogów
*   `commonTest`: Testy jednostkowe logiki biznesowej (wspólne dla iOS i Android).
*   `androidUnitTest`: Testy jednostkowe specyficzne dla Androida (np. z użyciem Robolectric dla małych komponentów, ale bez pełnego UI).
*   `androidInstrumentedTest`: Testy UI pełnoekranowe (Login, Flows) uruchamiane na urządzeniu. Tutaj znajduje się teraz `LoginUiTest`.
