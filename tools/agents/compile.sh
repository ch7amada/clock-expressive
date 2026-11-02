#!/usr/bin/env bash
# Compile the app's Kotlin sources and print only compiler errors (max 40 lines).
# Prints "COMPILE OK" on success. Used by coding agents; keep output short to save context.
cd "$(dirname "$0")/../.." || exit 1
ROOT="$(pwd)"
OUT="$(./gradlew :app:compileFossDebugKotlin -q --console=plain 2>&1)"
STATUS=$?
if [ $STATUS -eq 0 ]; then
  echo "COMPILE OK"
  exit 0
fi
ERRS="$(printf '%s\n' "$OUT" | grep -E '^e: ' | sed "s|file://$ROOT/||; s|$ROOT/||")"
if [ -n "$ERRS" ]; then
  printf '%s\n' "$ERRS" | head -40
  N=$(printf '%s\n' "$ERRS" | wc -l)
  [ "$N" -gt 40 ] && echo "... ($N errors total, fix the ones above first)"
else
  printf '%s\n' "$OUT" | grep -vE '^(w: |Hinweis|Note:|warning:)' | head -40
fi
exit 1
