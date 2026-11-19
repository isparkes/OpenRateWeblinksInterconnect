#!/usr/bin/env bash
#
# Zero to running: bring up the database, build the application, start it,
# push an hour of real call records through it, show what came out, and shut
# down again.
#
# Everything this does by hand is in db.sh / build.sh / run.sh / feed.sh /
# stop.sh.

set -euo pipefail
cd "$(dirname "$0")"
. ./common.sh

DATA_DIR=Data/WeblinksInterconnect
RUN_LOG=$(mktemp -t orweblinks)
FRAMEWORK_PID=""

cleanup() {
  if [ -n "$FRAMEWORK_PID" ] && kill -0 "$FRAMEWORK_PID" 2>/dev/null; then
    say ""
    step "Shutting the framework down"
    eci 'Framework:shutdown=true' >/dev/null 2>&1 || true
    for _ in $(seq 1 20); do
      framework_running || break
      sleep 1
    done
    kill "$FRAMEWORK_PID" 2>/dev/null || true
    wait "$FRAMEWORK_PID" 2>/dev/null || true
    say "Stopped. The database is still running - ./db.sh stop shuts it down."
  fi
  rm -f "$RUN_LOG"
}
trap cleanup EXIT INT TERM

check_toolchain
check_openrate_installed
check_db_tooling

if framework_running; then
  fail "Something is already listening on ${ECI_HOST}:${ECI_PORT}.
       Run ./stop.sh first, then try again."
fi

# --------------------------------------------------------------- 1. database
step "1/6  Bringing up MySQL"
./db.sh start | sed 's/^/  /'

# ------------------------------------------------------------------ 2. build
step "2/6  Building against OpenRate $(openrate_version)"
mvn -B -q clean package
say "    Built."

# ------------------------------------------------------------------ 3. clean
step "3/6  Clearing output from any previous run"
ensure_data_dirs
rm -f "${DATA_DIR}"/*.csv "${DATA_DIR}"/tmp*
for d in Done Err Dump WeblinksTraffic Filter InterconnectBilling \
         InterconnectReconciliation Error Duplicate AggResults; do
  find "${DATA_DIR}/${d}" -type f ! -name 'FileToForce*' -delete 2>/dev/null || true
done
printf 'delete from INTERCONNECT_BILLING_RESULTS;\ndelete from DUPLICATE_CHECK;\n' \
  | db_mysql "$DB_NAME"
say "    Output directories and previous billing results cleared."

# ------------------------------------------------------------------ 4. start
step "4/6  Starting the framework"
mvn -B exec:java > "$RUN_LOG" 2>&1 &
FRAMEWORK_PID=$!

for _ in $(seq 1 120); do
  grep -q 'Running\.\.\.' "$RUN_LOG" 2>/dev/null && break
  kill -0 "$FRAMEWORK_PID" 2>/dev/null || { sed 's/^/    /' "$RUN_LOG" | tail -20; fail "The framework exited during startup."; }
  sleep 1
done
grep -q 'Running\.\.\.' "$RUN_LOG" || { sed 's/^/    /' "$RUN_LOG" | tail -20; fail "The framework did not reach 'Running...' in 120s."; }

sed -n '/OpenRate V/,/Running\.\.\./p' "$RUN_LOG" | grep -E "OpenRate V|Copyright|pipeline|Running" | sed 's/^/    /'

# ------------------------------------------------------------------- 5. feed
step "5/6  Feeding one hour of call records"
./feed.sh 1 | sed 's/^/  /'

SAMPLE=$(ls "${DATA_DIR}"/cdr*.csv 2>/dev/null | head -1 | xargs -I{} basename {} .csv)
[ -n "$SAMPLE" ] || fail "feed.sh did not place a sample file in ${DATA_DIR}"

for _ in $(seq 1 90); do
  [ -f "${DATA_DIR}/Done/${SAMPLE}.done" ] && break
  sleep 1
done
[ -f "${DATA_DIR}/Done/${SAMPLE}.done" ] || fail "The file was not processed within 90s. Framework log:
$(tail -20 "$RUN_LOG")"

# the aggregation results are loaded by the second pipeline, give it a moment
for _ in $(seq 1 30); do
  [ "$(printf 'select count(*) from INTERCONNECT_BILLING_RESULTS;\n' \
        | db_mysql --silent --skip-column-names "$DB_NAME")" -gt 0 ] 2>/dev/null && break
  sleep 1
done

say "    Processed."

# ----------------------------------------------------------------- 6. result
step "6/6  What the pipelines did"
cat <<EXPLAIN

    WIPipeline read the call records, normalised the dialled number,
    worked out which operator the call came in from and went out to,
    looked up the destination zone, rated the call against the price
    model for that zone, checked it was not a duplicate, and wrote it
    to an output stream chosen by the interconnect scenario.

    $(wc -l < "${DATA_DIR}/Done/${SAMPLE}.done" | tr -d ' ') call records in, split across the output streams:

EXPLAIN
for d in WeblinksTraffic Filter InterconnectBilling InterconnectReconciliation Duplicate Error; do
  f="${DATA_DIR}/${d}/${SAMPLE}.out"
  if [ -f "$f" ]; then
    printf '      %-28s %s records\n' "$d" "$(wc -l < "$f" | tr -d ' ')"
  else
    printf '      %-28s -\n' "$d"
  fi
done

cat <<EXPLAIN

    One rated record, trimmed to its first six fields - scenario,
    partner, billing month, duration, charge, and the dialled number:

EXPLAIN
head -1 "${DATA_DIR}/InterconnectBilling/${SAMPLE}.out" 2>/dev/null \
  | cut -d';' -f1-6 | sed 's/^/      /'

cat <<EXPLAIN

    AggLoaderPipeline then picked up the aggregation results and wrote
    them to the database through a stored procedure. That is the
    interconnect bill for this traffic:

EXPLAIN
printf 'select PARTNER, BILLING_MONTH, SCENARIO, CDR_COUNT, SECOND_COUNT, round(COST,2) as COST_EUR from INTERCONNECT_BILLING_RESULTS order by PARTNER;\n' \
  | db_mysql --table "$DB_NAME" | sed 's/^/      /'

cat <<NEXT

--------------------------------------------------------------
 Next steps
--------------------------------------------------------------
  ./run.sh              start it and leave it running
  ./feed.sh 24          feed it a day of traffic instead of an hour
  ./feed.sh all         feed it all 2593 files (~4 months)
  ./db.sh status        show the interconnect billing results
  ./db.sh shell         poke around the database yourself
  ./stop.sh             shut the framework down
  ./db.sh stop          shut the database down

  telnet ${ECI_HOST} ${ECI_PORT}    admin console, 'Help' for commands

  Feeding more files accumulates into the same billing rows, because
  the aggregation is keyed on partner, month and scenario.
--------------------------------------------------------------
NEXT
