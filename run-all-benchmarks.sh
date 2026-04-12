#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
RESULTS_DIR="${ROOT_DIR}/benchmark-results"
MODE="${1:-full}"
TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
COMMON_ARGS=(-prof gc -rf json)

case "$MODE" in
  quick)
    RUN_ARGS=(-f 1 -wi 1 -i 1 -w 1s -r 1s)
    ;;
  full)
    RUN_ARGS=(-f 2 -wi 3 -i 5 -w 3s -r 5s)
    ;;
  --help|-h|help)
    cat <<'EOF'
Usage:
  ./run-all-benchmarks.sh [quick|full]

Modes:
  quick   Fast validation run
  full    More stable benchmark run (default)

Outputs:
  JSON results are written to ./benchmark-results/<timestamp>/
EOF
    exit 0
    ;;
  *)
    echo "Unknown mode: $MODE" >&2
    echo "Use: ./run-all-benchmarks.sh [quick|full]" >&2
    exit 1
    ;;
esac

mkdir -p "$RESULTS_DIR/$TIMESTAMP"

cd "$ROOT_DIR"

echo "==> Building benchmark jars"
./gradlew :benchmark-kserialization:jmhJar :benchmark-jackson2:jmhJar :benchmark-jackson3:jmhJar

echo "==> Running kotlinx.serialization benchmarks"
java -jar benchmark-kserialization/build/libs/benchmark-kserialization-1.0.0-jmh.jar   "${RUN_ARGS[@]}" "${COMMON_ARGS[@]}"   -rff "$RESULTS_DIR/$TIMESTAMP/kotlinx-results.json"

echo "==> Running Jackson 2 benchmarks"
java -jar benchmark-jackson2/build/libs/benchmark-jackson2-1.0.0-jmh.jar   "${RUN_ARGS[@]}" "${COMMON_ARGS[@]}"   -rff "$RESULTS_DIR/$TIMESTAMP/jackson2-results.json"

echo "==> Running Jackson 3 benchmarks"
java -jar benchmark-jackson3/build/libs/benchmark-jackson3-1.0.0-jmh.jar   "${RUN_ARGS[@]}" "${COMMON_ARGS[@]}"   -rff "$RESULTS_DIR/$TIMESTAMP/jackson3-results.json"

echo
echo "Done. Results written to:"
echo "  $RESULTS_DIR/$TIMESTAMP"
echo
echo "Allocation metric to inspect: gc.alloc.rate.norm (bytes/op)"

RUNS_FILE="$RESULTS_DIR/runs.json"
TMP_FILE=$(mktemp)
if [ -f "$RUNS_FILE" ]; then
  cp "$RUNS_FILE" "$TMP_FILE"
  if command -v python3 &> /dev/null; then
    python3 -c "
import json, sys
with open('$TMP_FILE', 'r') as f:
    runs = json.load(f)
exists = any(r['runId'] == '$TIMESTAMP' for r in runs)
if not exists:
    runs.append({'runId': '$TIMESTAMP', 'timestamp': '$(date -Iseconds)', 'note': ''})
print(json.dumps(runs, indent=2))
" > "$RUNS_FILE"
  else
    echo "[]" > "$RUNS_FILE"
  fi
else
  echo "[{\"runId\": \"$TIMESTAMP\", \"timestamp\": \"$(date -Iseconds)\", \"note\": \"\"}]" > "$RUNS_FILE"
fi
rm -f "$TMP_FILE"
echo "Updated $RUNS_FILE"
