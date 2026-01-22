## 2026-01-22 - Added Loading State to BeerWallButton
**Learning:** Adding a built-in `isLoading` state to primary buttons dramatically improves perceived performance and usability compared to full-screen blocking dialogs. It keeps the context of the action.
**Action:** When creating new interactive components, always consider "pending" or "loading" states as first-class citizens of the component API.
