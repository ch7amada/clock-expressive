# Progress log

| Feature | Status | Coder rounds | Review verdict | Commit |
|---|---|---|---|---|
| F00 Build setup + agent tooling | done (PM) | – | – | see git log |
| F01 Theme (MaterialExpressiveTheme, dynamic color, Google Sans Flex) | done | 1 + 1 fix | CHANGES_REQUESTED → fixed (font family on emphasized styles, unremembered state) | see git log |
| F02 Shape kit (morph shapes, press squish, ShapeBadge, MorphingPlayPauseButton) | done | 1 + 1 fix | CHANGES_REQUESTED → fixed (squish order, icon rotating, per-frame recomposition) | see git log |
| F03 Shared components (EmptyState, AnimatedTimeText, dialogs, segmented lists) | done | 1 + 1 fix | CHANGES_REQUESTED → fixed (dialogs had no container, missing SegmentedGap, lazy key collision) | see git log |
| F04 App shell (ShortNavigationBar / WideNavigationRail, navigation-compose, Compose MainActivity) | done | 1 + 1 resume + 1 fix (+1 resume) | CHANGES_REQUESTED → fixed (stale tab-request closure, last-tab persistence on Back, wide-layout insets) | see git log |
| F05 Clock tab hero (breathing hero time, seconds pill, next-alarm chip, world-clock rows) | done | 1 + 1 fix | APPROVE (minor polish: scale-in rerun on scroll, haptics, pill shape, DateTimeFormatter) | see git log |
| F06 Analogue clock face + digital/analogue toggle | **implemented, NOT reviewed** | 1 | pending — next: `tools/agents/run.sh reviewer F06`, PM addendum, fix round if needed | see git log |

## Paused 2026-11-03 — resume here
1. Review F06 (`tools/agents/run.sh reviewer F06`), PM addendum, `run.sh fix F06` if needed, screenshot both faces
   (`tools/qa/shot.sh … --tab clock`, tap the clock), then amend the PROGRESS row.
   Coder note: no `onClickLabel` semantics property exists in this Compose version — it used an equivalent; check it.
2. F07 (time zones) → completes M2 → fast-forward `main`, tag `m2-clock`.
3. F08–F19 per `docs/rewrite/tasks/`; still to write: F20 (cleanup), F21 (polish/a11y), F22 (README/screenshots).
Keep `tools/agents/monitor.sh` armed while agents run.
