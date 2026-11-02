#!/usr/bin/env bash
# Live monitor for agent runs. Prints one line per notable event:
#  - new agent run started / finished (from .agent-logs)
#  - file writes/edits, compile results, errors, watchdog actions in the active log
#  - STALL: an agent process is running but llama-server generated nothing for $STALL_MIN minutes
cd "$(dirname "$0")/../.." || exit 1
HOST="${LLAMA_HOST:-ahmed@192.168.178.172}"
STALL_MIN="${STALL_MIN:-8}"
declare -A seen
last_active=$(date +%s); stall_reported=0; cur=""; was_running=0
while true; do
  running=0; pgrep -f "opencode run --standalone --agent" >/dev/null && running=1
  newest=$(ls -t .agent-logs/*.log 2>/dev/null | head -1)
  if [ -n "$newest" ] && [ "$newest" != "$cur" ]; then cur="$newest"; echo "RUN $(basename "$cur" .log) started"; fi
  if [ -n "$cur" ]; then
    n=${seen[$cur]:-0}
    total=$(wc -l < "$cur")
    if [ "$total" -gt "$n" ]; then
      tail -n +"$((n+1))" "$cur" | sed 's/\x1b\[[0-9;]*m//g' \
        | grep -E "^(← |✗ )|Write |Edit |COMPILE OK|^e: |Error|watchdog" | cut -c1-160 \
        | sed "s/^/[$(basename "$cur" .log)] /"
      seen[$cur]=$total
    fi
  fi
  # model heartbeat: any generation/prompt timing line in the last minute?
  if ssh -o BatchMode=yes -o ConnectTimeout=5 "$HOST" \
      "journalctl --user -u llama-server --since '-70s' --no-pager 2>/dev/null | grep -q 'print_timing\|launch_slot'"; then
    last_active=$(date +%s); stall_reported=0
  fi
  if [ $running = 1 ]; then
    idle=$(( ($(date +%s) - last_active) / 60 ))
    if [ "$idle" -ge "$STALL_MIN" ] && [ $stall_reported = 0 ]; then
      echo "STALL: agent running but llama-server idle for ${idle} min ($(basename "$cur" .log))"; stall_reported=1
    fi
  else
    last_active=$(date +%s)
  fi
  if [ $was_running = 1 ] && [ $running = 0 ]; then echo "RUN $(basename "$cur" .log) finished"; fi
  was_running=$running
  sleep 60
done
