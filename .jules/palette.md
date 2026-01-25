## 2026-01-22 - Added Loading State to BeerWallButton
**Learning:** Adding a built-in `isLoading` state to primary buttons dramatically improves perceived performance and usability compared to full-screen blocking dialogs. It keeps the context of the action.
**Action:** When creating new interactive components, always consider "pending" or "loading" states as first-class citizens of the component API.

## 2026-01-23 - Enhanced Keyboard Interactions
**Learning:** Users naturally expect the "Done" key on the soft keyboard to submit simple forms. Explicitly handling `ImeAction.Done` via `KeyboardActions` creates a seamless flow, removing the friction of manually hiding the keyboard to find a submit button.
**Action:** Always configure `keyboardOptions` (especially `ImeAction`) and `keyboardActions` for text inputs, ensuring the "Enter" key performs the primary positive action.

## 2026-01-25 - Icon-Only Status Indicators
**Learning:** In `CardItemView`, status icons (check/cancel) were displayed without any accompanying text to prevent layout overflow, but lacked `contentDescription`, making them invisible to screen readers.
**Action:** Always verify that icon-only status indicators have dynamic `contentDescription`s reflecting their state (e.g., "Active" vs "Blocked").
