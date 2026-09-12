#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"

GRADLE_VERSION="9.2.1"
CACHE_DIR="$PWD/.local-gradle"
GRADLE_DIR="$CACHE_DIR/gradle-$GRADLE_VERSION"
ZIP="$CACHE_DIR/gradle-$GRADLE_VERSION-bin.zip"

if ! command -v java >/dev/null 2>&1; then
  echo "Java was not found. Install/use Java 21, then run this again."
  exit 1
fi

echo "Using: $(java -version 2>&1 | head -n 1)"
mkdir -p "$CACHE_DIR"

if [ ! -x "$GRADLE_DIR/bin/gradle" ]; then
  echo "Downloading Gradle $GRADLE_VERSION locally (no admin install needed)..."
  curl -L --fail --progress-bar \
    "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" \
    -o "$ZIP"
  rm -rf "$GRADLE_DIR"
  unzip -q "$ZIP" -d "$CACHE_DIR"
fi

"$GRADLE_DIR/bin/gradle" build

echo
echo "Build finished. Mod JAR:"
ls -1 build/libs/underworld-*.jar | grep -v -- '-sources.jar' || true
