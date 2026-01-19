## 2024-05-22 - Password Visibility Toggle
**Learning:** `BeerWallTextField` strictly enforces `PasswordVisualTransformation` when `isPassword` is true, preventing toggle implementation.
**Action:** When implementing password toggle, keep `isPassword = false` on `BeerWallTextField` and manually manage `visualTransformation` and `trailingIcon`.
