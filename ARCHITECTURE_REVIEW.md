# Opinia o Architekturze Projektu BeerWall

## Podsumowanie
Projekt jest zbudowany solidnie, wykorzystując nowoczesne podejście Kotlin Multiplatform (KMP) oraz deklaratywny UI (Compose Multiplatform). Architektura podąża za wzorcem Clean Architecture z podziałem na warstwy (Domain, Data, Presentation, UI), co jest dużym plusem. Zidentyfikowano jednak kilka kluczowych naruszeń zasad czystej architektury, które mogą utrudniać skalowanie i testowanie aplikacji.

## Co jest zrobione DOBRZE ✅

1. **Podział na warstwy (Clean Architecture)**
   - Istnieje wyraźny podział na `domain`, `data`, `presentation` i `ui`.
   - Logika biznesowa jest zamknięta w `UseCases`, co ułatwia testowanie i ponowne użycie kodu.

2. **Kotlin Multiplatform (KMP)**
   - Współdzielenie kodu (`commonMain`) jest zmaksymalizowane (UI, ViewModel, domena, dane).
   - Platformowe specyficzności są poprawnie wydzielone (np. `Platform.kt`).

3. **Zarządzanie Zależnościami (Dependency Injection)**
   - Ręczne wstrzykiwanie zależności (`AppContainer`) jest proste i wystarczające dla tej skali, unikając narzutu bibliotek takich jak Hilt/Koin (choć przy większej skali Koin mógłby być rozważony).
   - Wzorzec Repository jest poprawnie zaimplementowany, ukrywając źródła danych przed domeną.

4. **Konfiguracja Builda**
   - Użycie `BuildKonfig` do zarządzania zmiennymi środowiskowymi (np. `BASE_URL`) to bardzo dobra praktyka.
   - Version Catalog (`libs.versions.toml`) jest używany do spójnego zarządzania wersjami bibliotek.

## Co wymaga POPRAWY (Złamania Architektury) ❌

1. **Wyciek DTO do warstwy UI (Poważny błąd)**
   - **Problem:** Klasa `AppUiState` importuje bezpośrednio `PaymentMethod` z pakietu `remote.dto`.
   - **Dlaczego to źle:** Warstwa prezentacji (UI) nie może wiedzieć o strukturze danych z API. Zmiana w API (np. zmiana nazwy pola w JSON) nie powinna wymuszać zmian w UI.
   - **Rozwiązanie:** Należy stworzyć model domenowy (w `domain/model`) lub model UI (w `ui/models`) i mapować dane w warstwie Data lub Presentation.

2. **Struktura Pakietów - `remote` poza `data`**
   - **Problem:** Pakiet `remote` znajduje się w głównym katalogu (`org.fruex.beerwall.remote`), na równi z `domain` i `data`.
   - **Dlaczego to źle:** Logicznie `remote` jest częścią warstwy danych (`data layer`). Powinien znajdować się w `org.fruex.beerwall.data.remote`. Obecna struktura sugeruje, że `remote` jest niezależną warstwą architektoniczną.

3. **God Object - `AppViewModel`**
   - **Problem:** `AppViewModel` agreguje w sobie wszystkie inne ViewModele (`Auth`, `Balance`, `Cards` itd.) i zarządza całym stanem aplikacji.
   - **Dlaczego to źle:** Łamie zasadę Single Responsibility Principle. Klasa jest trudna w utrzymaniu, testowaniu i powoduje, że każda zmiana w dowolnym module wymaga modyfikacji tego pliku. Wszystkie ekrany zależą od jednego gigantycznego stanu.
   - **Rozwiązanie:** Należy dążyć do tego, aby każdy ekran (Feature) miał swój własny ViewModel, a komunikacja między nimi odbywała się przez parametry nawigacji lub obserwację wspólnych źródeł danych (Repository/UseCase), a nie przez wspólny ViewModel.

4. **Hardcoded Stringi w `BaseApiClient`**
   - **Problem:** W klientach API (np. `AuthApiClient`) ścieżki URL są budowane przez konkatenację stringów (`"$baseUrl/mobile/auth/signIn"`).
   - **Dlaczego to źle:** Podatne na błędy (literówki).
   - **Rozwiązanie:** Warto rozważyć wydzielenie ścieżek do stałych lub osobnego obiektu konfiguracyjnego `ApiRoutes`.

5. **Bezpośrednie użycie `ViewModel` w `AppContainer`**
   - Kontener DI tworzy instancje ViewModeli. W architekturze Android/Compose zazwyczaj ViewModele powinny być tworzone przez `ViewModelProvider` (lub odpowiednik w KMP, np. `viewModel { ... }` z bibliotek DI), aby przetrwać zmiany konfiguracji. Tutaj tworzone są zwykłe obiekty, co w czystym KMP jest OK, ale trzeba uważać na cykl życia.

## Rekomendacje (Plan Naprawczy)

1. **Refaktoryzacja Pakietów:** Przesunięcie `remote` do `data/remote`.
2. **Izolacja UI:** Stworzenie modelu domenowego dla `PaymentMethod` i usunięcie zależności do DTO z `AppUiState`.
3. **Refaktoryzacja ViewModel:** Oznaczenie miejsc do poprawy w `AppViewModel` i zaplanowanie migracji na dedykowane ViewModele per ekran.
