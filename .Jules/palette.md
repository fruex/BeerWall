## 2024-05-22 - Empty States & Redundant Descriptions
**Learning:** Users are often left with a confusing blank screen when a list is empty. Adding a dedicated component that explains the empty state and points to the primary action (even if the button is visible elsewhere) improves confidence.
**Action:** Always check `LazyColumn` or `LazyRow` for an `if (items.isEmpty())` branch and provide a friendly illustration or text.

## 2024-05-22 - Accessibility Double Talk
**Learning:** Redundant `contentDescription` on icons next to text labels creates "double talk" for screen readers (e.g., "Status: Active, Active").
**Action:** Always set `contentDescription = null` for icons that are purely decorative or immediately accompanied by text that conveys the same meaning.
