# Przegląd Architektury BeerWall

## Podsumowanie
Kod źródłowy projektu BeerWall jest zorganizowany zgodnie z zasadami Clean Architecture i MVVM. Wykorzystuje nowoczesne podejście do tworzenia aplikacji multiplatformowych (KMP).

## Pozytywne Aspekty
*   **Zarządzanie Zależnościami (DI):** Projekt poprawnie wykorzystuje bibliotekę Koin. Wcześniejsza manualna implementacja (`AppContainer`) została usunięta, co upraszcza zarządzanie cyklem życia obiektów i testowanie.
*   **Warstwa Prezentacji:** Monolityczny `AppViewModel` został skutecznie zdekomponowany na mniejsze, wyspecjalizowane ViewModele (np. `BalanceViewModel`, `AuthViewModel`). Jest to zgodne z zasadą Single Responsibility Principle.
*   **Struktura Pakietów:** Pakiety są logicznie podzielone na warstwy (`domain`, `data`, `presentation`, `ui`). Pakiet `remote` znajduje się poprawnie wewnątrz `data`, co odzwierciedla jego rolę jako źródła danych.
*   **Konfiguracja:** Adresy URL API są zarządzane przez `BuildKonfig` i `ApiRoutes`, unikając "magic strings" w kodzie klientów.

## Obszary do Poprawy
*   **Naruszenie Granic Warstw (Krytyczne):**
    *   `AppUiState` oraz `BalanceUiState` importują bezpośrednio `PaymentMethod` z warstwy `data.remote.dto`. Jest to naruszenie zasad Clean Architecture. Warstwa UI nie powinna mieć wiedzy o obiektach DTO (Data Transfer Objects). Wymaga to wprowadzenia modelu UI (`UiPaymentMethod`) i odpowiedniego mappera.
*   **Martwy Kod:**
    *   Klasa `AppUiState` wydaje się być pozostałością po refaktoryzacji (nie jest nigdzie używana poza definicją). Zalecane jest jej usunięcie lub oznaczenie jako `@Deprecated`, jeśli planowane jest jej przyszłe użycie.
*   **Mapowanie:** Brakuje dedykowanych mapperów dla niektórych modeli domenowych na modele UI, co wymusza (jak w przypadku `PaymentMethod`) używanie obiektów z niższych warstw.

## Plan Naprawczy (Wdrożony w tym PR)
1.  Utworzenie modelu `UiPaymentMethod` w warstwie UI.
2.  Utworzenie mappera `PaymentMethodMapper` w warstwie prezentacji.
3.  Refaktoryzacja `BalanceViewModel` i `AppUiState` w celu użycia nowych modeli UI, eliminując zależność od DTO.
