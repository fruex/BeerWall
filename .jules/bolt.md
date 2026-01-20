## 2024-05-23 - [Build Integrity Check]
**Learning:** The codebase contained a pre-existing compilation error (`VenueBalance` vs `PremisesBalance`) which blocked verification.
**Action:** Always run a quick compile check (`compileCommonMainKotlinMetadata`) before starting optimizations to establish a baseline.
