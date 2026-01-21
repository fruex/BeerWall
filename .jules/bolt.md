## 2024-05-23 - [Build Integrity Check]
**Learning:** The codebase contained a pre-existing compilation error (`VenueBalance` vs `PremisesBalance`) which blocked verification.
**Action:** Always run a quick compile check (`compileCommonMainKotlinMetadata`) before starting optimizations to establish a baseline.

## 2025-05-23 - [UI Render Loop Optimization]
**Learning:** Formatting strings (date parsing) inside `LazyColumn` items adds unnecessary overhead during scrolling.
**Action:** Move data formatting logic (like `startDateTime` -> `formattedTime`) to the UI Mapper (background/one-time) and expose a pre-formatted field in the UI Model.
