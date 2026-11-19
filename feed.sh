#!/usr/bin/env bash
#
# Give the running framework some call detail records to rate.
#
#   ./feed.sh          one hour of CDRs  - the default, a few thousand records
#   ./feed.sh 24       one day of CDRs
#   ./feed.sh all      everything in the archive (2593 files, ~4 months)
#
# The samples live compressed in Data/WeblinksInterconnect/CDRsFromMSC.tar.bz2,
# which is a real extract from the operator's MSC. Decompressing the archive
# takes a few seconds, so the file listing is cached after the first run.

set -euo pipefail
cd "$(dirname "$0")"
. ./common.sh

DATA_DIR=Data/WeblinksInterconnect
ARCHIVE="${DATA_DIR}/CDRsFromMSC.tar.bz2"
INDEX="${DATA_DIR}/.cdr-index"

[ -f "$ARCHIVE" ] || fail "Missing sample archive ${ARCHIVE}"

count="${1:-1}"
case "$count" in
  all)             ;;
  ''|*[!0-9]*)     fail "Usage: $0 [number-of-files|all]" ;;
  0)               fail "Nothing to do - ask for at least one file." ;;
esac

ensure_data_dirs

if [ ! -s "$INDEX" ]; then
  step "Indexing ${ARCHIVE} (first run only, takes a few seconds)"
  tar -tjf "$ARCHIVE" | grep '\.csv$' | sort > "$INDEX"
  say "    $(wc -l < "$INDEX" | tr -d ' ') sample files available."
fi

available=$(wc -l < "$INDEX" | tr -d ' ')
if [ "$count" = all ]; then
  members=$(cat "$INDEX")
  count=$available
else
  [ "$count" -le "$available" ] || fail \
    "Only ${available} sample files are available, ${count} asked for."
  members=$(head -n "$count" "$INDEX")
fi

framework_running || say \
  "Note: nothing is listening on ${ECI_HOST}:${ECI_PORT}, so the framework
      does not look like it is running. The files will be picked up whenever
      you start it with ./run.sh"

step "Extracting ${count} CDR file(s) into ${DATA_DIR}"
# The input adapter matches cdr*.csv in this directory, so extracting a file
# is what triggers processing. --strip-components drops the CDRsFromMSC/
# directory from the archive so the files land where the adapter looks.
# The member list goes through a file rather than a pipe, because bsdtar
# (the tar on macOS) will not read "-T -" from standard input.
MEMBER_LIST=$(mktemp -t orwi-members)
trap 'rm -f "$MEMBER_LIST"' EXIT
printf '%s\n' "$members" > "$MEMBER_LIST"
tar -xjf "$ARCHIVE" -C "$DATA_DIR" --strip-components=1 -T "$MEMBER_LIST"

say "    $(printf '%s\n' "$members" | sed 's|.*/||' | head -3 | tr '\n' ' ')$([ "$count" -gt 3 ] && printf '... (%s files)' "$count")"
say ""
say "The pipeline renames each input to Done/*.done and writes rated records"
say "into the per-partner directories under ${DATA_DIR}."
say "Aggregated results go to the database:  ./db.sh status"
