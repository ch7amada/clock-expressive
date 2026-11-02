#!/usr/bin/env bash
# Usage: tools/agents/run.sh <coder|reviewer|fix> <TASK_ID> [extra instructions]
# Runs one opencode agent session on the local llama.cpp models and logs it to .agent-logs/.
set -u
cd "$(dirname "$0")/../.." || exit 1
ROLE="$1"; TASK="$2"; EXTRA="${3:-}"
SPEC="docs/rewrite/tasks/$TASK.md"
REVIEW="docs/rewrite/reviews/$TASK.md"
[ -f "$SPEC" ] || { echo "missing $SPEC"; exit 1; }
mkdir -p .agent-logs
N=1; while [ -e ".agent-logs/$TASK-$ROLE-$N.log" ]; do N=$((N+1)); done
LOG=".agent-logs/$TASK-$ROLE-$N.log"

case "$ROLE" in
  coder)
    AGENT=coder
    PROMPT="Your task: implement $SPEC. Read AGENTS.md rules (already loaded) and the task file first. $EXTRA"
    ;;
  reviewer)
    AGENT=reviewer
    git add -N . 2>/dev/null   # make new files visible to `git diff`
    PROMPT="Review the implementation of task $TASK. Spec: $SPEC. The change is the uncommitted working tree: run \`git diff --stat\` then \`git diff\` (per file if large). Read the old code it replaces where the spec names it. Then write $REVIEW using exactly this format:
# Review $TASK (round $N)
Verdict: APPROVE | CHANGES_REQUESTED
## Findings
1. [BLOCKER|MAJOR|MINOR] path/File.kt:line — problem — required change
## Acceptance criteria
- [x]/[ ] each criterion from the spec, one line each
$EXTRA"
    ;;
  fix)
    AGENT=coder
    PROMPT="Task $SPEC was implemented (uncommitted changes, see \`git diff --stat\`) and reviewed. Address every BLOCKER and MAJOR finding in $REVIEW (MINOR ones too if cheap). Read the review first, then only the files it mentions. Run tools/agents/compile.sh until COMPILE OK. $EXTRA"
    ;;
  *) echo "unknown role $ROLE"; exit 1;;
esac

echo "[$(date +%T)] $ROLE $TASK -> $LOG"
START=$(date +%s)
timeout 80m opencode run --agent "$AGENT" --title "$TASK-$ROLE-$N" "$PROMPT" > "$LOG" 2>&1
RC=$?
echo "[$(date +%T)] done rc=$RC in $(( ($(date +%s)-START)/60 )) min; log tail:"
tail -15 "$LOG"
exit $RC
