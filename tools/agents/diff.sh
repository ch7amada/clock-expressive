#!/usr/bin/env bash
# Read-only view of the uncommitted change for agents.
#   bash tools/agents/diff.sh            -> summary (git status + diff --stat incl. new files)
#   bash tools/agents/diff.sh <path>     -> full diff of one file (new files shown completely)
cd "$(dirname "$0")/../.." || exit 1
if [ -z "$1" ]; then
  git status --short -- app/ docs/rewrite/legacy 2>/dev/null
  echo "---"
  git add -N app/ 2>/dev/null
  git diff --stat -- app/
else
  git add -N -- "$1" 2>/dev/null
  git diff -- "$1"
fi
