#!/bin/bash
set -euo pipefail

GODOT_VERSION="4.7.2"
GODOT_ARCHIVE="Godot_v${GODOT_VERSION}-stable_macos.universal.zip"
GODOT_URL="https://github.com/godotengine/godot/releases/download/${GODOT_VERSION}-stable/${GODOT_ARCHIVE}"
GODOT_SHA256="c58a24e31d720be9d62f60cb5627c4e695fb72f21b0cfe1bc9ccaa9a3b3ba63e"

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_PATH="$SCRIPT_DIR"
if [[ $# -gt 0 && "$1" != --* ]]; then
    PROJECT_PATH="$1"
    shift
fi
if [[ ! -f "$PROJECT_PATH/project.godot" ]]; then
    echo "Error: no se encontró project.godot en: $PROJECT_PATH" >&2
    exit 1
fi
PROJECT_PATH="$(cd -- "$PROJECT_PATH" && pwd)"

godot_has_expected_version() {
    local executable="$1"
    local version
    [[ -x "$executable" ]] || return 1
    version="$("$executable" --version 2>/dev/null)" || return 1
    [[ "$version" == "$GODOT_VERSION"* ]]
}

GODOT_EXECUTABLE=""
if [[ -n "${GODOT_BIN:-}" ]]; then
    if ! godot_has_expected_version "$GODOT_BIN"; then
        echo "Error: GODOT_BIN debe apuntar a Godot ${GODOT_VERSION}: $GODOT_BIN" >&2
        exit 1
    fi
    GODOT_EXECUTABLE="$GODOT_BIN"
elif command -v godot >/dev/null 2>&1 && godot_has_expected_version "$(command -v godot)"; then
    GODOT_EXECUTABLE="$(command -v godot)"
elif godot_has_expected_version "/Applications/Godot.app/Contents/MacOS/Godot"; then
    GODOT_EXECUTABLE="/Applications/Godot.app/Contents/MacOS/Godot"
elif godot_has_expected_version "$HOME/Applications/Godot_v${GODOT_VERSION}-stable.app/Contents/MacOS/Godot"; then
    GODOT_EXECUTABLE="$HOME/Applications/Godot_v${GODOT_VERSION}-stable.app/Contents/MacOS/Godot"
else
    INSTALL_DIR="$HOME/Applications"
    INSTALL_APP="$INSTALL_DIR/Godot_v${GODOT_VERSION}-stable.app"
    TEMP_DIR="$(mktemp -d "${TMPDIR:-/tmp}/dungeonquest-godot.XXXXXX")"
    trap 'rm -rf "$TEMP_DIR"' EXIT
    ARCHIVE_PATH="$TEMP_DIR/$GODOT_ARCHIVE"

    echo "Godot ${GODOT_VERSION} no está instalado. Descargando la versión oficial para macOS..."
    curl --fail --location --retry 3 --connect-timeout 20 "$GODOT_URL" --output "$ARCHIVE_PATH"

    ACTUAL_SHA256="$(shasum -a 256 "$ARCHIVE_PATH" | awk '{print $1}')"
    if [[ "$ACTUAL_SHA256" != "$GODOT_SHA256" ]]; then
        echo "Error: la verificación SHA-256 de la descarga falló." >&2
        exit 1
    fi

    unzip -tq "$ARCHIVE_PATH"
    unzip -q "$ARCHIVE_PATH" -d "$TEMP_DIR"
    if [[ ! -x "$TEMP_DIR/Godot.app/Contents/MacOS/Godot" ]]; then
        echo "Error: el archivo descargado no contiene Godot.app." >&2
        exit 1
    fi

    mkdir -p "$INSTALL_DIR"
    ditto "$TEMP_DIR/Godot.app" "$INSTALL_APP"
    GODOT_EXECUTABLE="$INSTALL_APP/Contents/MacOS/Godot"
    if ! godot_has_expected_version "$GODOT_EXECUTABLE"; then
        echo "Error: Godot no pudo instalarse correctamente en $INSTALL_APP" >&2
        exit 1
    fi
    echo "Godot ${GODOT_VERSION} quedó instalado en $INSTALL_APP."
fi

echo "Iniciando Dungeon Quest con Godot ${GODOT_VERSION}..."
exec "$GODOT_EXECUTABLE" --path "$PROJECT_PATH" "$@"
