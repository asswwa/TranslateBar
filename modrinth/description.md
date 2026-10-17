# TranslateBar

A tiny client-side Fabric mod that translates whatever you type in Minecraft chat — right inside the chat bar, without leaving the game.

## Features

- **Two compact buttons built into the chat input row.** `文` translates, the language chip (`EN`, `DE`, …) switches the target language.
- **Any source language.** The source is detected automatically — type Russian, English, Japanese, whatever.
- **10 target languages:** EN, DE, FR, ES, PL, ZH, RU, JA, IT, PT.
- **Speaks your language.** Tooltips and status messages are shown in English or Russian, depending on your game language.
- **You stay in control.** The translation replaces the text in the input box; review it and press Enter yourself. Commands starting with `/` are never translated.
- **Zero setup.** No API keys, no config files.
- **Never freezes the game.** Translation requests run in the background.
- **Matches your chat.** The buttons share the chat bar height and follow your chat background opacity setting.

## Supported versions

Pick the file that matches your game version:

- **26.1, 26.1.1, 26.1.2** — `translatebar-1.1.0+26.1.jar`
- **26.2** — `translatebar-1.1.0+26.2.jar`
- **26.3** — `translatebar-1.1.0+26.3.jar`
- **1.21.11** — `translatebar-1.1.0+1.21.11.jar`

## Requirements

- **Fabric Loader 0.19.5+** and **Fabric API** for your game version.
- **Java 25** for the 26.x builds, **Java 21** for the 1.21.11 build — launchers usually manage this automatically.

## How to use

1. Drop Fabric API and the matching TranslateBar file into your `mods` folder.
2. Open chat (`T`) and type your message.
3. Click `文` — the input is replaced with the translation.
4. Click the language chip to cycle through target languages. The choice lasts until you close the game.

## Building from source

One Gradle project builds every game line (JDK 25 and Gradle required):

```bash
bash tools/build_all.sh   # all versions → modrinth/files/
gradle build              # 26.1.x line only
```

## Disclosures

**Data & privacy.** Pressing the translate button sends the current text of the chat input over HTTPS to Google's public translation endpoint (`translate.googleapis.com`) so it can be translated. Nothing else is sent — no account, no chat history, no telemetry. If you never press the button, no network requests are made.

**AI-generated content.** This project was created by the author together with generative AI tools (ChatGPT and OpenCode). All product decisions, design choices, and in-game testing were done by the author; a substantial portion of the code was produced with AI assistance.

## License

MIT. See the [LICENSE](LICENSE) file.
