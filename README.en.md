# TranslateBar

A client-side Fabric mod for Minecraft Java. Two translucent buttons right in the chat bar: `文` translates the text from the input box with one click, the language-code button (`EN`, `DE`, …) switches the target language. The source language is detected automatically — type Russian, English, Japanese, anything.

Supported game versions: **1.21.11, 26.1, 26.1.1, 26.1.2, 26.2, 26.3** — one JAR per game line.

> **Русский:** [README.md](README.md) · [текст страницы проекта](modrinth/description.ru.md).

## Features

- Black translucent buttons styled after the chat row: same height and position as the input bar, opacity follows the in-game text background setting.
- 10 target languages: English, German, French, Spanish, Polish, Chinese, Russian, Japanese, Italian, Portuguese.
- Tooltips and status messages are shown in English or Russian, depending on your game language.
- The translation is inserted into the input box but never sent — review it and press Enter.
- Commands (`/…`) and empty input are not translated.
- Requests run in the background — the game never hangs.
- No API keys, no config files.

## Installation

1. Fabric Loader and Fabric API for your game version — into the `mods` folder.
2. Download the TranslateBar JAR matching your game version (see "Building" below or the release files) and drop it there too.
3. In game: open chat with `T` → type your text → click `文`.

## Building

Requires JDK 25 and Gradle. All game lines build from one project:

```bash
bash tools/build_all.sh   # all versions → modrinth/files/
gradle build              # 26.1.x line only → build/libs/translatebar-1.1.0.jar
```

Ready file names: `translatebar-1.1.0+26.1.jar` (covers 26.1–26.1.2),
`translatebar-1.1.0+26.2.jar`, `translatebar-1.1.0+26.3.jar`, `translatebar-1.1.0+1.21.11.jar`.
The mod version is pinned to 1.1.0 on purpose, so a JAR can be replaced without duplicates in `mods`.

## Disclosures

**Data.** Pressing the translate button sends the text from the chat input over HTTPS to Google's public translation service (`translate.googleapis.com`). Nothing else is sent. No network requests are made unless you press the button.

**AI.** This project was created by the author together with generative AI tools (ChatGPT and OpenCode): all product decisions, design choices and in-game testing are the author's; a substantial part of the code was written with AI assistance.

## License

MIT (see the [LICENSE](LICENSE) file).
