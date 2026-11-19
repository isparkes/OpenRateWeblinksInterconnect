#!/usr/bin/env bash
#
# Shared helpers for the OpenRateWeblinksInterconnect distribution scripts.
# Not meant to be run directly.

REQUIRED_JAVA=17
ECI_HOST="${OPENRATE_ECI_HOST:-localhost}"
ECI_PORT="${OPENRATE_ECI_PORT:-8086}"

# The database this application rates against. The connection details are
# hard coded in properties/WeblinksInterconnect.properties.xml, so the
# defaults here have to match what is configured there.
DB_NAME=WeblinksInterconnectDB
DB_HOST="${OPENRATE_DB_HOST:-localhost}"
DB_PORT="${OPENRATE_DB_PORT:-3306}"
DB_USER="${OPENRATE_DB_USER:-root}"
DB_PASS="${OPENRATE_DB_PASS:-cpr}"

# docker - bin/db.sh runs MySQL in a container (the default)
# local  - bin/db.sh talks to a MySQL you already run, using the mysql client
DB_MODE="${OPENRATE_DB_MODE:-docker}"
DB_CONTAINER="${OPENRATE_DB_CONTAINER:-openrate-weblinks-db}"
DB_IMAGE="${OPENRATE_DB_IMAGE:-mysql:8.4}"

say()  { printf '%s\n' "$*"; }
fail() { printf 'ERROR: %s\n' "$*" >&2; exit 1; }

# Absolute path of the installation root (the parent of bin/).
openrate_home() {
  local here
  here="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  dirname "$here"
}

# Locate a JDK of at least REQUIRED_JAVA, honouring JAVA_HOME if it is set.
resolve_java() {
  local candidate
  if [ -n "${JAVA_HOME:-}" ] && [ -x "${JAVA_HOME}/bin/java" ]; then
    candidate="${JAVA_HOME}/bin/java"
  elif command -v java >/dev/null 2>&1; then
    candidate="$(command -v java)"
  else
    fail "No Java found. OpenRate needs JDK ${REQUIRED_JAVA} or later.
       Set JAVA_HOME, or install one:
         macOS:  brew install --cask temurin@${REQUIRED_JAVA}
         Linux:  apt install openjdk-${REQUIRED_JAVA}-jdk"
  fi

  local raw major
  raw=$("$candidate" -version 2>&1 | head -1 | sed -n 's/.*version "\([^"]*\)".*/\1/p')
  case "$raw" in
    1.*) major=$(printf '%s' "$raw" | cut -d. -f2) ;;
    *)   major=$(printf '%s' "$raw" | cut -d. -f1) ;;
  esac
  if [ -z "$major" ] || [ "$major" -lt "$REQUIRED_JAVA" ]; then
    fail "Found Java ${raw:-unknown} at ${candidate}, but ${REQUIRED_JAVA} or later is needed.
       Point JAVA_HOME at a newer JDK, for example:
         export JAVA_HOME=\$(/usr/libexec/java_home -v ${REQUIRED_JAVA})   # macOS"
  fi
  printf '%s' "$candidate"
}

# The classpath for the unpacked distribution.
#   properties  holds the configuration, which OpenRate loads from the classpath
#   dist        this application
#   ORlib       the OpenRate framework
#   lib         third party dependencies
openrate_classpath() {
  printf '%s' "properties:dist/*:ORlib/*:lib/*"
}

# True while something is accepting connections on host:port.
port_open() {
  local host="$1" port="$2"
  if command -v nc >/dev/null 2>&1; then
    nc -z "$host" "$port" >/dev/null 2>&1
  else
    (exec 3<>"/dev/tcp/${host}/${port}") >/dev/null 2>&1
  fi
}

framework_running() {
  port_open "$ECI_HOST" "$ECI_PORT"
}

# Send one command to the admin console and echo the reply.
eci() {
  local cmd="$1"
  command -v nc >/dev/null 2>&1 || fail \
    "'nc' is not available, so this script cannot reach the admin console.
       Connect by hand instead:  telnet ${ECI_HOST} ${ECI_PORT}"
  { printf '%s\n' "$cmd"; sleep 2; } | nc "$ECI_HOST" "$ECI_PORT" 2>/dev/null
}

# ---------------------------------------------------------------- database --

check_db_tooling() {
  case "$DB_MODE" in
    docker)
      command -v docker >/dev/null 2>&1 || fail \
        "No 'docker' on the PATH, and OPENRATE_DB_MODE is 'docker'.
       Install Docker, or point the scripts at a MySQL you already run:
           export OPENRATE_DB_MODE=local"
      docker info >/dev/null 2>&1 || fail \
        "Docker is installed but not running. Start Docker and try again."
      ;;
    local)
      command -v mysql >/dev/null 2>&1 || fail \
        "No 'mysql' client on the PATH, and OPENRATE_DB_MODE is 'local'.
       Install the MySQL client, or let the scripts run MySQL for you:
           export OPENRATE_DB_MODE=docker"
      ;;
    *) fail "OPENRATE_DB_MODE must be 'docker' or 'local', not '${DB_MODE}'" ;;
  esac
}

db_container_running() {
  [ "$(docker inspect -f '{{.State.Running}}' "$DB_CONTAINER" 2>/dev/null)" = "true" ]
}

# Run a mysql client command against the application database, reading SQL
# from stdin. Connects over TCP in both modes: while the container image is
# initialising it runs a temporary server with networking disabled, and
# connecting to that one would make the database look ready too early.
db_mysql() {
  if [ "$DB_MODE" = docker ]; then
    docker exec -i -e MYSQL_PWD="$DB_PASS" "$DB_CONTAINER" \
      mysql --protocol=tcp --host=127.0.0.1 --user="$DB_USER" "$@"
  else
    MYSQL_PWD="$DB_PASS" mysql --host="$DB_HOST" --port="$DB_PORT" \
          --user="$DB_USER" "$@"
  fi
}

db_answering() {
  printf 'select 1;\n' | db_mysql --silent >/dev/null 2>&1
}

db_loaded() {
  local count
  count=$(printf 'select count(*) from ZONE_MODEL;\n' \
            | db_mysql --silent --skip-column-names "$DB_NAME" 2>/dev/null) || return 1
  [ -n "$count" ] && [ "$count" -gt 0 ] 2>/dev/null
}

check_database() {
  check_db_tooling

  if [ "$DB_MODE" = docker ] && ! db_container_running; then
    fail "The MySQL container '${DB_CONTAINER}' is not running.
       Start it and load the schema with:  bin/db.sh start"
  fi

  db_answering || fail \
    "No MySQL answering on ${DB_HOST}:${DB_PORT}.
       Start it and load the schema with:  bin/db.sh start"

  db_loaded || fail \
    "MySQL is up, but ${DB_NAME} has no reference data.
       Load it with:  bin/db.sh load"
}

# The working directories the pipelines write into.
ensure_data_dirs() {
  local d
  for d in Done Err Dump WeblinksTraffic Filter InterconnectBilling \
           InterconnectReconciliation Error Duplicate AggResults; do
    mkdir -p "Data/WeblinksInterconnect/${d}"
  done
  mkdir -p log Semaphore ConfigData/WeblinksInterconnect
}
