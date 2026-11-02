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

# Qwen reasoning effort (chat-template variant defined in opencode.json): xhigh is the model default;
# medium is much faster with little quality loss. Override with QWEN_EFFORT=low|medium|xhigh.
QWEN_MODEL="drago/qwen3.8-27b#${QWEN_EFFORT:-medium}"
[ "${QWEN_EFFORT:-medium}" = "xhigh" ] && QWEN_MODEL="drago/qwen3.8-27b"
MODEL_ARGS=()

case "$ROLE" in
  coder)
    AGENT=coder; MODEL_ARGS=(-m "$QWEN_MODEL")
    PROMPT="Your task: implement $SPEC. Read AGENTS.md rules (already loaded) and the task file first. $EXTRA"
    ;;
  reviewer)
    AGENT=reviewer
    git add -N . 2>/dev/null   # make new files visible to `git diff`
    PROMPT="Review the implementation of task $TASK. Spec: $SPEC. The change is the uncommitted working tree: run \`bash tools/agents/diff.sh\` for the summary, then \`bash tools/agents/diff.sh <path>\` for each changed file. Read the old code it replaces where the spec names it. Then write $REVIEW using exactly this format:
# Review $TASK (round $N)
Verdict: APPROVE | CHANGES_REQUESTED
## Findings
1. [BLOCKER|MAJOR|MINOR] path/File.kt:line — problem — required change
## Acceptance criteria
- [x]/[ ] each criterion from the spec, one line each
$EXTRA"
    ;;
  fix)
    AGENT=coder; MODEL_ARGS=(-m "$QWEN_MODEL")
    PROMPT="Task $SPEC was implemented (uncommitted changes, see \`bash tools/agents/diff.sh\`) and reviewed. Address every BLOCKER and MAJOR finding in $REVIEW (MINOR ones too if cheap). Read the review first, then only the files it mentions. Run bash tools/agents/compile.sh until COMPILE OK. $EXTRA"
    ;;
  *) echo "unknown role $ROLE"; exit 1;;
esac

# Watchdog: a run whose log does not grow for STALL_MIN minutes is killed and retried (max 1 retry).
STALL_MIN="${STALL_MIN:-25}"
run_once() {
  timeout 80m opencode run --standalone --agent "$AGENT" "${MODEL_ARGS[@]}" --title "$TASK-$ROLE-$N" "$PROMPT" >> "$LOG" 2>&1 &
  local pid=$! last_size=-1 idle=0
  while kill -0 "$pid" 2>/dev/null; do
    sleep 60
    local size; size=$(stat -c %s "$LOG" 2>/dev/null || echo 0)
    if [ "$size" = "$last_size" ]; then idle=$((idle+1)); else idle=0; last_size=$size; fi
    if [ "$idle" -ge "$STALL_MIN" ]; then
      echo "[watchdog] no progress for $STALL_MIN min, killing" >> "$LOG"
      pkill -P "$pid" 2>/dev/null; kill "$pid" 2>/dev/null; wait "$pid" 2>/dev/null
      return 124
    fi
  done
  wait "$pid"
}

echo "[$(date +%T)] $ROLE $TASK -> $LOG"
START=$(date +%s)
run_once; RC=$?
if [ $RC -eq 124 ]; then echo "[$(date +%T)] stalled, retrying once"; run_once; RC=$?; fi
echo "[$(date +%T)] done rc=$RC in $(( ($(date +%s)-START)/60 )) min; log tail:"
tail -15 "$LOG"
exit $RC
