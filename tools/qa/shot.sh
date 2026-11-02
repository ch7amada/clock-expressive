#!/usr/bin/env bash
# Usage: tools/qa/shot.sh <out.png> [--no-build] [--tab clock|alarm|stopwatch|timer] [--dark]
# Builds the foss debug APK, installs it on the running emulator/device, launches it and takes a screenshot.
set -e
cd "$(dirname "$0")/../.."
OUT="$1"; shift
BUILD=1; TAB=""; DARK=""
while [ $# -gt 0 ]; do case "$1" in --no-build) BUILD=0;; --tab) TAB="$2"; shift;; --dark) DARK=1;; esac; shift; done
ADB="${ANDROID_HOME:-$HOME/Android/Sdk}/platform-tools/adb"
PKG=org.fossify.clock.debug
if [ $BUILD = 1 ]; then ./gradlew -q assembleFossDebug 2>&1 | grep -E "^e: |FAILED|What went wrong" || true; fi
"$ADB" install -r app/build/outputs/apk/foss/debug/*.apk >/dev/null
if [ -n "$DARK" ]; then "$ADB" shell cmd uimode night yes; else "$ADB" shell cmd uimode night no; fi
"$ADB" shell am force-stop $PKG
case "$TAB" in clock) T=1;; alarm) T=2;; stopwatch) T=4;; timer) T=8;; *) T="";; esac
if [ -n "$T" ]; then
  "$ADB" shell am start -n $PKG/org.fossify.clock.activities.MainActivity --ei open_tab $T >/dev/null
else
  "$ADB" shell monkey -p $PKG -c android.intent.category.LAUNCHER 1 >/dev/null 2>&1
fi
sleep "${SHOT_DELAY:-5}"
"$ADB" exec-out screencap -p > "$OUT"
echo "saved $OUT"
