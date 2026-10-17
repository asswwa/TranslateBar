#!/usr/bin/env bash
# Собирает TranslateBar для всех поддерживаемых линий Minecraft и складывает
# готовые файлы в modrinth/files/. Запуск из корня проекта:
#
#     bash tools/build_all.sh
#
# Нужны JDK 25 и Gradle (см. README.md).
set -euo pipefail
cd "$(dirname "$0")/.."

mkdir -p modrinth/files

# build <суффикс> <mc-версия> <fabric-api> <диапазон игры> <совместимость> [доп. -P...]
build() {
    local suffix="$1" mc="$2" api="$3" range="$4" compat="$5"
    shift 5
    echo "########## TranslateBar для $suffix ##########"
    gradle build -Pminecraft_version="$mc" -Pfabric_version="$api" \
        -Pminecraft_range="$range" -Pmc_compat="$compat" "$@" --console=plain | grep -E "BUILD (SUCCESSFUL|FAILED)"
    cp build/libs/translatebar-1.1.0.jar "modrinth/files/translatebar-1.1.0+$suffix.jar"
    cp build/libs/translatebar-1.1.0-sources.jar "modrinth/files/translatebar-1.1.0+$suffix-sources.jar"
}

# Линия 26.1 — один JAR покрывает 26.1, 26.1.1 и 26.1.2 (внутри заявлено ~26.1).
build 26.1 26.1.2 "0.155.3+26.1.2" "~26.1" legacy

# 26.2 и 26.3 — по одному JAR на версию.
build 26.2 26.2 "0.161.0+26.2" "~26.2" modern
build 26.3 26.3 "0.163.0+26.3" "~26.3" modern

# Легаси-линия 1.21.11 — классический Loom с ремапом, Java 21.
build 1.21.11 1.21.11 "0.141.6+1.21.11" "~1.21.11" mc1211 \
    -Plegacy_loom -Pjava_release=21 -Pmixin_compat=JAVA_21 -Pjava_dep=21

echo
echo "Готово. Файлы для загрузки: modrinth/files/"
ls -la modrinth/files/
