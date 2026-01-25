## 2026-01-25 - Icon-Only Status Indicators
**Learning:** In `CardItemView`, status icons (check/cancel) were displayed without any accompanying text to prevent layout overflow, but lacked `contentDescription`, making them invisible to screen readers.
**Action:** Always verify that icon-only status indicators have dynamic `contentDescription`s reflecting their state (e.g., "Active" vs "Blocked").
