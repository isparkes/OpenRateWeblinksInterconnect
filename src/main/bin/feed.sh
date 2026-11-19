#!/usr/bin/env bash
#
# Give a running OpenRateWeblinksInterconnect some call records to rate.
#
#   bin/feed.sh            the small sample, one quiet hour (168 records)
#   bin/feed.sh large      the large sample, one busy hour (~3000 records)
#   bin/feed.sh <file.gz>  any gzipped CDR file of your own
#
# The full archive of 2593 hourly files is not shipped in the distribution
# because of its size. It is in the source tree, under
# Data/WeblinksInterconnect/CDRsFromMSC.tar.bz2.

set -euo pipefail

. "$(cd "$(dirname "$0")" && pwd)/common.sh"
cd "$(openrate_home)"

DATA_DIR="Data/WeblinksInterconnect"

case "${1:-small}" in
  small) source_file="samples/cdrFromMSC_2008_02_10_00.csv.gz" ;;
  large) source_file="samples/cdrFromMSC_2008_05_05_21.csv.gz" ;;
  *)     source_file="$1" ;;
esac

[ -f "$source_file" ] || fail "No such sample: ${source_file}
       Shipped samples:
$(ls samples/*.gz 2>/dev/null | sed 's|^|         |' || echo '         (none)')"

framework_running || say \
  "Note: nothing is listening on ${ECI_HOST}:${ECI_PORT}, so OpenRate does not
      look like it is running. The file will be picked up when you start it."

ensure_data_dirs
target="${DATA_DIR}/$(basename "${source_file%.gz}")"
gzip -dc "$source_file" > "$target"

say "Fed ${target}"
say "The rated records appear in the per-scenario directories under ${DATA_DIR},"
say "and the aggregated bill goes to the database:  bin/db.sh status"
