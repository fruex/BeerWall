# Audyt Architektoniczny Projektu BeerWall

## Podsumowanie Wykonawcze

Projekt wykazuje **dobre zrozumienie ogólnych zasad Clean Architecture** w kontekście Kotlin Multiplatform (KMP). Podział na warstwy `domain`, `data`, `presentation` i `ui` jest widoczny i w większości przypadków przestrzegany.

Jednakże, znaleziono kilka **krytycznych naruszeń** zasad warstwowości, które psują niezależność domeny i wprowadzają dług techniczny. Największym problemem jest "wyciekanie" szczegółów implementacyjnych (Data, UI) do warstw wyższych (Domain, Presentation) w specyficznych obszarach (głównie Autoryzacja).

Ocena ogólna: **Dobry z uwagami (B-)**. Projekt jest solidny, ale wymaga refaktoryzacji w module autoryzacji, aby być w pełni zgodnym ze sztuką.

---

## Szczegółowa Analiza Warstw

### 1. Warstwa Domeny (`domain`)

**Co jest dobrze:**
*   Modele domeny (np. `Card`) są w większości czystymi klasami Kotlin (POJO/POGO).
*   Logika biznesowa jest ukryta za interfejsami (UseCase, Repository).
*   Brak bezpośrednich importów z warstw `data` czy `ui` (z wyjątkiem Auth, patrz niżej).

**Co wymaga poprawy:**
*   **Zależność od `auth`:** `GoogleSignInUseCase` importuje `com.fruex.beerwall.auth.GoogleAuthProvider`. Pakiet `auth` zawiera kod zależny od Compose (`@Composable`), co sprawia, że Domena pośrednio zależy od frameworku UI. To naruszenie zasady czystości domeny.
*   **Adnotacje `@Serializable`:** Niektóre modele domeny (np. `AuthTokens`) używają adnotacji `kotlinx.serialization`. Choć jest to pragmatyczne w KMP, w ścisłym Clean Architecture modele domeny powinny być agnostyczne względem formatu danych.

### 2. Warstwa Prezentacji (`presentation`)

**Co jest dobrze:**
*   Większość ViewModeli (np. `BalanceViewModel`) komunikuje się wyłącznie z warstwą Domeny poprzez UseCase.
*   Stan UI jest poprawnie zarządzany przez `StateFlow`.
*   Użycie mapperów (`toUi()`) do konwersji modeli domeny na modele widoku.

**Co wymaga poprawy:**
*   **Krytyczny błąd w `AuthViewModel`:** ViewModel ten bezpośrednio importuje i używa `com.fruex.beerwall.data.local.TokenManager`. Jest to **obejście warstwy domeny** i bezpośredni dostęp do warstwy danych z warstwy prezentacji. Logika ta powinna znajdować się w UseCase (np. `GetSessionProfileUseCase`).
*   **Logika w `CardsViewModel`:** Bezpośrednie użycie `NfcRepository` zamiast dedykowanego UseCase. Choć mniejszy błąd, łamie konwencję stosowaną w reszcie aplikacji.

### 3. Warstwa Danych (`data`)

**Co jest dobrze:**
*   Repozytoria (`CardRepositoryImpl`) poprawnie mapują obiekty DTO (z API) na modele domeny.
*   Implementacja `Repository Pattern` jest poprawna.

**Co wymaga poprawy:**
*   Struktura pakietu `auth` jest niejasna (patrz sekcja "Problem pakietu Auth").

### 4. Problem Pakietu `auth` (`com.fruex.beerwall.auth`)

Pakiet ten jest "sierotą" architektoniczną. Zawiera:
*   Interfejsy (`GoogleAuthProvider`) - powinny być w Domenie.
*   Implementacje logiki (`SessionManager`) - powinny być w Data.
*   Kod UI (`rememberGoogleAuthProvider`) - powinien być w UI/Presentation.
*   Encje (`GoogleUser`) - powinny być w Domenie lub Data.

Obecnie pakiet ten jest "wspólny", co prowadzi do sytuacji, gdzie Domena zależy od kodu leżącego obok niej, który z kolei zależy od Compose.

---

## Rekomendacje Naprawcze

### Priorytet 1: Naprawa `AuthViewModel`
Należy usunąć zależność `TokenManager` z `AuthViewModel`.
*   **Rozwiązanie:** Stwórz `GetUserProfileUseCase` (lub rozszerz `CheckSessionUseCase`), który pobierze dane użytkownika z `TokenManager` (przez Repository) i zwróci je do ViewModelu.

### Priorytet 2: Restrukturyzacja `auth` (Google Auth)
W odpowiedzi na Twoje pytanie o `GoogleAuth` dla małej/średniej aplikacji:

**Zalecana strategia:** Pełna separacja (Clean Architecture).
Nawet w średniej aplikacji, mieszanie `@Composable` z logiką biznesową używaną przez Domenę jest ryzykowne (np. utrudnia testy jednostkowe domeny bez frameworku Android/Compose).

**Plan działania:**
1.  **Przenieś Interfejs:** `GoogleAuthProvider` i `GoogleUser` (oczyszczony z logiki weryfikacji JWT, jeśli to możliwe, lub logika przeniesiona do serwisu) przenieś do `domain/repository` (lub `domain/auth`).
2.  **Przenieś Implementację UI:** `rememberGoogleAuthProvider` i implementację interfejsu przenieś do `ui/auth` lub `presentation/auth`.
3.  **Wstrzykiwanie:** W `MainActivity` lub module DI, dostarcz implementację z UI do warstwy Domeny (lub przekaż jako parametr do UseCase wywoływanego z UI).

Dzięki temu `GoogleSignInUseCase` będzie zależeć tylko od interfejsu wewnątrz Domeny, a nie od zewnętrznego pakietu z `@Composable`.

### Priorytet 3: Ujednolicenie ViewModeli
Zastąp bezpośrednie użycie `NfcRepository` w `CardsViewModel` nowym `ObserveNfcScanUseCase`.

---

## Lista Kontrolna Zgodności

| Obszar | Ocena | Uwagi |
| :--- | :---: | :--- |
| **Separacja Warstw** | ⚠️ | Wyciek Data do Presentation (`AuthViewModel`), Wyciek UI do Domain (`auth`). |
| **Niezależność Domeny** | ⚠️ | Zależność od `auth` (Compose) i `serialization`. |
| **Zarządzanie Stanem** | ✅ | Poprawne użycie `StateFlow` i `uiState`. |
| **Obsługa Błędów** | ✅ | Użycie `Result<>` i mapowanie błędów w ViewModel. |
| **Testowalność** | ⚠️ | Trudne testowanie `GoogleSignInUseCase` przez statyczne metody/mixiny w `GoogleAuth`. |

---
*Wygenerowano przez Jules (AI Software Engineer)*
