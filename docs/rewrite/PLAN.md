# Clock Expressive — Compose + Material 3 Expressive rewrite plan

Fork of [Fossify Clock](https://github.com/FossifyOrg/Clock) (GPL-3.0). Goal: replace the entire
View/XML UI with Jetpack Compose built on **Material 3 Expressive** (material3 `1.5.0-beta02`),
keeping the proven data/scheduling layer untouched.

## Roles

| Role | Who | Why |
|---|---|---|
| Project manager, architecture, build config, task specs, integration, visual QA, commits | Claude (orchestrator) | holds the whole picture |
| **Coder** — writes all feature code | `qwen3.8-27b` (llama.cpp on drago, 112K ctx) via opencode agent `coder` | strongest coder; context is tight, so every task is small and self-contained |
| **Reviewer** — reviews every feature diff against its spec | `ternary-bonsai-2-27b` (256K ctx) via opencode agent `reviewer` | 2-bit model, weaker at writing code, but its 256K context fits a whole diff plus every related file and the spec at once |

Only one model fits in VRAM at a time; llama-server swaps on demand (~15 s), so the coder → reviewer → coder
loop is cheap.

## Per-feature workflow

1. PM writes `docs/rewrite/tasks/Fxx.md` — goal, exact files to read, files to create or modify, API references,
   acceptance criteria. Budget: about 35K tokens of reading for the coder.
2. `tools/agents/run.sh coder Fxx` — Qwen implements and runs `tools/agents/compile.sh` until it is green.
3. PM gate: compile, skim the diff, enforce scope.
4. `tools/agents/run.sh reviewer Fxx` — Bonsai writes `docs/rewrite/reviews/Fxx.md`
   (verdict `APPROVE` or `CHANGES_REQUESTED`, numbered findings).
5. If changes are requested: `tools/agents/run.sh fix Fxx` — Qwen, in a fresh context, addresses the review. At most two rounds.
6. PM: build the APK, take an emulator screenshot, and commit (`feat(ui): Fxx …`) with the task and the review
   included. Push.

## Architecture (target)

```
org.fossify.clock
├── (unchanged) models/ databases/ helpers/ services/ receivers/ interfaces/ extensions/
└── ui/
    ├── theme/        ClockTheme (MaterialExpressiveTheme), color, type (Google Sans Flex), shapes
    ├── components/   shared expressive building blocks (morphing shapes, weekday picker, sound picker…)
    ├── navigation/   ClockApp scaffold, adaptive nav (ShortNavigationBar / WideNavigationRail), NavHost
    ├── clock/        ClockScreen + ClockViewModel, analogue face, time-zone pickers
    ├── alarm/        AlarmScreen + AlarmViewModel, alarm editor
    ├── stopwatch/    StopwatchScreen + StopwatchViewModel
    ├── timer/        TimerScreen + TimerViewModel, timer editor
    ├── ringing/      full-screen alarm UI (AlarmActivity), snooze picker
    ├── settings/     SettingsScreen
    └── about/        AboutScreen
```

* Single-activity UI: `MainActivity` stays a `SimpleActivity` (keeps Fossify permission helpers) but uses `setContent`.
* ViewModels (`AndroidViewModel`) wrap the existing `dbHelper`, `timerHelper`, `config`, `Stopwatch` and EventBus
  events, and expose `StateFlow`. Business logic is moved out of fragments and adapters unchanged.
* Services, receivers, notifications, Room, and `AlarmController` are **not touched** except where a UI hook is
  required.
* Widgets stay RemoteViews; only their configuration activities move to Compose.

## Feature backlog

| ID | Feature | Main outputs |
|---|---|---|
| F00 | Build: Compose BOM 2026.09, material3 1.5.0-beta02, nav, graphics-shapes, reorderable, Google Sans Flex, compileSdk 37 | gradle — *PM* |
| F01 | Theme: `ClockTheme` (dynamic color + expressive fallback, light/dark/system, pure black), Google Sans Flex typography, shapes; Config keys | `ui/theme/*` |
| F02 | Shape kit: `MorphPolygonShape`, `animatedMorphShape()`, `ShapeBadge`, expressive press-squish modifier | `ui/components/Shapes.kt` |
| F03 | Shared components: `ExpressiveEmptyState`, `SectionHeader`, `ConfirmDialog`, `RadioListDialog`, `TimeText` (animated digits) | `ui/components/*` |
| F04 | App shell: `ClockApp` adaptive scaffold, 4 tabs, flexible top app bar + overflow, NavHost, placeholder screens; MainActivity → Compose (intents, shortcuts, keep-screen-on, last tab) | `ui/navigation/*`, MainActivity |
| F05 | Clock tab: `ClockViewModel`, hero digital clock (variable-font animated digits), date, next-alarm chip, time-zone list (segmented items) | `ui/clock/*` |
| F06 | Analogue clock face (Cookie12Sided dial, animated hands), digital/analog toggle | `ui/clock/AnalogClock.kt` |
| F07 | Time-zone management: add-zones search sheet, edit title dialog, swipe to remove | `ui/clock/*` |
| F08 | Alarm list: `AlarmViewModel` (sorting, toggle, delete, EventBus refresh), alarm cards, empty state, large FAB | `ui/alarm/*` |
| F09 | Sound picker (system alarm sounds, custom file via SAF, preview playback) — shared with timers | `ui/components/SoundPicker.kt` |
| F10 | Alarm editor sheet: M3 TimePicker, weekday ButtonGroup, label, sound, vibrate, snooze, delete | `ui/alarm/AlarmEditor.kt` |
| F11 | Alarm sort dialog + drag reorder (custom sort) | `ui/alarm/*` |
| F12 | Stopwatch: ViewModel, hero time, wavy progress ring, morphing play/pause, lap/reset group, laps list (best/worst), lap sort | `ui/stopwatch/*` |
| F13 | Timer list: ViewModel, timer cards with CircularWavyProgressIndicator, morph play/pause, reset, delete | `ui/timer/*` |
| F14 | Timer editor: expressive numeric keypad duration input, label, sound, vibrate | `ui/timer/TimerEditor.kt` |
| F15 | Ringing screen: AlarmActivity → Compose (pulsing morph shape, slide to dismiss / snooze), snooze picker activity | `ui/ringing/*` |
| F16 | Settings screen (grouped segmented lists): theme, language, default tab, hour format, keep-awake, week start, reminders, snooze, gradual volume, widget colors, export/import | `ui/settings/*` |
| F17 | About screen (expressive, credits upstream Fossify, licenses incl. OFL font) | `ui/about/*` |
| F18 | Widget configuration activities → Compose | `ui/widget/*` |
| F19 | Permission and dialog polish: Compose permission rationale dialogs replace the View-based PermissionRequiredDialog | `ui/components/*` |
| F20 | Cleanup: delete fragments, adapters, View dialogs, unused layouts and deps; detekt and lint | repo-wide |
| F21 | Polish: motion pass, haptics, accessibility (content descriptions, 48dp targets, TalkBack), landscape/tablet, predictive back | repo-wide |
| F22 | README, screenshots, release notes | docs |

Milestones (each merged to `main` and pushed):
**M1** F00–F04 (themed shell) · **M2** F05–F07 (clock) · **M3** F08–F11 (alarms) · **M4** F12–F14 (stopwatch + timer)
· **M5** F15–F19 (ringing, settings, about, widgets) · **M6** F20–F22 (cleanup, polish, release).
