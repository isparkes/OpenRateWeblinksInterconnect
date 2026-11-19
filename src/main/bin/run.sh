#!/usr/bin/env bash
#
# Start OpenRateWeblinksInterconnect from an unpacked distribution.
#
#   bin/run.sh [properties-file]
#
# The properties file defaults to WeblinksInterconnect.properties.xml and is
# resolved from the classpath, which includes the properties/ directory.
#
# Environment:
#   JAVA_HOME           JDK to use (must be 17 or later)
#   JAVA_OPTS           extra JVM options, e.g. -Xmx2g
#   OPENRATE_ECI_PORT   admin console port (default 8086)

set -euo pipefail

. "$(cd "$(dirname "$0")" && pwd)/common.sh"

HOME_DIR="$(openrate_home)"
cd "$HOME_DIR"

JAVA_BIN="$(resolve_java)"
PROPERTIES="${1:-WeblinksInterconnect.properties.xml}"

[ -f "properties/${PROPERTIES}" ] || fail \
  "No properties/${PROPERTIES} in ${HOME_DIR}.
       Available:
$(ls properties/*.properties.xml 2>/dev/null | sed 's|^|         |' || echo '         (none)')"

check_database
ensure_data_dirs

if framework_running; then
  fail "Something is already listening on ${ECI_HOST}:${ECI_PORT}.
       OpenRate may already be running - try bin/stop.sh first."
fi

cat <<BANNER
--------------------------------------------------------------
 OpenRateWeblinksInterconnect
--------------------------------------------------------------
 Home           ${HOME_DIR}
 Configuration  properties/${PROPERTIES}
 Database       ${DB_NAME} on ${DB_HOST}:${DB_PORT}
 Java           $("$JAVA_BIN" -version 2>&1 | head -1)

 Runs until stopped. Once you see "Running...", from another
 terminal in this directory:

     bin/feed.sh      rate a sample hour of call records
     bin/db.sh status show the interconnect billing results
     bin/stop.sh      shut down cleanly

 Admin console:  telnet ${ECI_HOST} ${ECI_PORT}
--------------------------------------------------------------

BANNER

# Relative paths in the configuration resolve against the installation root,
# so this must run from there.
exec "$JAVA_BIN" ${JAVA_OPTS:-} -cp "$(openrate_classpath)" \
     OpenRate.OpenRate -p "$PROPERTIES"
