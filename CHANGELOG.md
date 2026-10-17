# Changelog

## [1.1.0] — 2026-10-17

First public release.

- Black translucent "pill" buttons integrated into the chat bar: exact bar height (12 px),
  opacity follows the in-game text background setting, subtle top shine, soft shadow,
  light rim on hover, click sound, left click only.
- `文` translates the current chat input; the language chip cycles 10 target languages
  (EN, DE, FR, ES, PL, ZH, RU, JA, IT, PT). The choice lasts until the game is closed.
- Source language is auto-detected, so any language can be translated.
- Translation is inserted into the input box, never sent automatically; commands (`/…`)
  and empty input are skipped with an on-screen hint.
- Interface tooltips and hints follow your game language: Russian for a Russian game,
  English otherwise (language files `en_us`/`ru_ru`).
- Network requests run in the background and never freeze the game.
- One download per game line: **26.1.x** (covers 26.1, 26.1.1, 26.1.2), **26.2**, **26.3** and legacy
  **1.21.11** — pick the file matching your game version.
- Requires Fabric Loader 0.19.5+, Fabric API and Java 25 (Java 21 for the 1.21.11 build).
