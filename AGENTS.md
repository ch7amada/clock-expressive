# AGENTS.md — rules for coding agents in this repo

Android clock app (fork of Fossify Clock). The View/XML UI is being rewritten in **Jetpack Compose + Material 3
Expressive**. Plan: `docs/rewrite/PLAN.md`. Your assignment is always one task file: `docs/rewrite/tasks/Fxx.md`.

## Hard rules
1. Do **only** what your task file asks. Do not touch files the task does not list, unless a compile error forces a
   one-line fix elsewhere.
2. Your context window is small (~110K tokens). Read only the files the task lists. For big files use `grep -n` and
   read line ranges (`sed -n '120,200p' file`). Never read the whole `res/values*/strings.xml` — grep it.
3. Never edit the data layer: `models/`, `databases/`, `helpers/` (except `helpers/Config.kt` when the task says so),
   `services/`, `receivers/`, `interfaces/`.
4. After writing code, run `tools/agents/compile.sh` and fix every error until it prints `COMPILE OK`.
   Do not run other gradle commands. Do not run git commands that change state (no commit, checkout, reset, stash).
5. When finished, reply with a short summary: files created or changed, and anything you could not do.

## Tech stack (already configured — do not change gradle files)
Kotlin 2.4, compileSdk 37, minSdk 26, Compose BOM 2026.09, **material3 1.5.0-beta02**, navigation-compose 2.10,
lifecycle-viewmodel-compose, androidx.graphics:graphics-shapes 1.1, material-icons-extended,
`sh.calvin.reorderable` 3.1. `ExperimentalMaterial3Api` and `ExperimentalMaterial3ExpressiveApi` are opted in
globally, so **do not** add `@OptIn` for them.

## Package layout (new code goes under `app/src/main/kotlin/org/fossify/clock/ui/`)
`theme/` `components/` `navigation/` `clock/` `alarm/` `stopwatch/` `timer/` `ringing/` `settings/` `about/` `widget/`

## Material 3 Expressive design language (follow it)
- Wrap UI in `ClockTheme { }` (from `ui/theme`). Use `MaterialTheme.colorScheme`, `.typography`, `.shapes`,
  `.motionScheme` tokens only — no hard-coded colors or sizes for text.
- Motion: animate with `MaterialTheme.motionScheme.defaultSpatialSpec()` / `fastSpatialSpec()` (position, size, shape)
  and `defaultEffectsSpec()` (color, alpha). Springy, never linear.
- Shape: big, soft, and varied. Cards `MaterialTheme.shapes.extraLarge`; decorative containers use
  `MaterialShapes.*` (e.g. `Cookie9Sided`, `Sunny`, `Cookie12Sided`, `Pill`) via `.toShape()`. Interactive elements
  morph shape on press or state (see `ui/components/Shapes.kt` once it exists).
- Typography: Google Sans Flex variable font (`R.font.google_sans_flex`). Big numbers use the display styles from
  `ui/theme/Type.kt`.
- Components: prefer the expressive ones — `ShortNavigationBar`, `WideNavigationRail`, `LargeFlexibleTopAppBar`,
  `MediumFloatingActionButton`, `ButtonGroup` + `ToggleButton`, `SegmentedListItem`, `CircularWavyProgressIndicator`,
  `LoadingIndicator`, `HorizontalFloatingToolbar`, `SplitButtonLayout`, `IconButton(shapes = …)`, `Button(shapes = …)`.
- Haptics on toggles and important actions: `LocalHapticFeedback.current.performHapticFeedback(...)`.
- Accessibility: every icon-only control has a `contentDescription` from string resources; touch targets ≥ 48dp.

## API reference — material3 1.5.0-beta02 is newer than your training data
Exact public signatures are in `docs/rewrite/api/<Component>.md` (e.g. `ButtonGroup.md`, `ToggleButton.md`,
`ListItem.md`, `AppBar.md`, `FloatingActionButton.md`, `WavyProgressIndicator.md`, `ShortNavigationBar.md`,
`WideNavigationRail.md`, `TimePicker.md`, `MaterialShapes.md`, `FloatingToolbar.md`, `IconButton.md`, `Button.md`).
**Grep them before using an expressive API — do not guess parameter names.**
`MaterialShapes` members: Circle Square Slanted Arch Fan Arrow SemiCircle Oval Pill Triangle Diamond ClamShell Pentagon
Gem Sunny VerySunny Cookie4Sided Cookie6Sided Cookie7Sided Cookie9Sided Cookie12Sided Ghostish Clover4Leaf Clover8Leaf
Burst SoftBurst Boom SoftBoom Flower Puffy PuffyDiamond PixelCircle PixelTriangle Bun Heart (all `RoundedPolygon`,
normalized to a 0..1 box).

## Existing code you will reuse (do not rewrite it)
- `extensions/Context.kt`: `config`, `dbHelper`, `timerHelper`, `alarmController`, `getFormattedTime`,
  `createNewAlarm`, `createNewTimer`, `updateWidgets`, `getClosestEnabledAlarmString`, `rotateWeekdays`, …
- `helpers/Config.kt` (prefs, extends Fossify `BaseConfig`: also `use24HourFormat`, `firstDayOfWeek`, `snoozeTime`,
  `vibrateOnButtonPress`, …), `helpers/Constants.kt` (keys, tab ids, sort constants).
- EventBus events in `models/AlarmEvent.kt`, `models/TimerEvent.kt`, which drive refreshes.
- Strings: reuse existing resources first (`grep -n 'name="alarm' app/src/main/res/values/strings.xml`, and Fossify
  commons strings via `org.fossify.commons.R.string.*`). New strings go **only** in
  `app/src/main/res/values/strings.xml` (English), at the end, prefixed `m3e_`.

## Code style
Small composables, stateless where possible (state hoisted to a ViewModel), `Modifier` as first optional parameter,
`@Preview` for leaf components. No `!!` unless the old code had it. No new third-party dependencies.
