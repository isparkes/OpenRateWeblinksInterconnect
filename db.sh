#!/usr/bin/env bash
#
# Bring up the MySQL database this application rates against, and load its
# schema and reference data.
#
#   ./db.sh            start MySQL if needed, then load the schema (= start)
#   ./db.sh start      same
#   ./db.sh load       (re)load the schema and reference data
#   ./db.sh status     say whether the database is up and loaded
#   ./db.sh shell      open a mysql prompt on the application database
#   ./db.sh stop       stop the database
#   ./db.sh reset      throw the database away and start over
#
# By default MySQL runs in a Docker container, so there is nothing to install.
# To use a MySQL you already run instead:
#
#   export OPENRATE_DB_MODE=local
#   export OPENRATE_DB_USER=root OPENRATE_DB_PASS=yourpassword
#
# The connection details are hard coded in WeblinksInterconnect.properties.xml,
# so a server on another host or port needs that file changed to match.

set -euo pipefail
cd "$(dirname "$0")"
. ./common.sh

SCHEMA=src/main/db-backups/WeblinksInterconnectDB.sql
PROCEDURES=ConfigData/WeblinksInterconnect/SP-Procedure-Create.sql

db_start() {
  if [ "$DB_MODE" = local ]; then
    db_answering || fail \
      "No MySQL answering on ${DB_HOST}:${DB_PORT} as user '${DB_USER}'.
       Start your own server, or let the scripts run one:
           export OPENRATE_DB_MODE=docker"
    say "    MySQL is answering on ${DB_HOST}:${DB_PORT}."
    return
  fi

  if db_container_running; then
    say "    Container '${DB_CONTAINER}' is already running."
  elif docker inspect "$DB_CONTAINER" >/dev/null 2>&1; then
    say "    Starting the existing container '${DB_CONTAINER}'."
    docker start "$DB_CONTAINER" >/dev/null
  else
    if port_open "$DB_HOST" "$DB_PORT"; then
      fail "Something is already listening on ${DB_HOST}:${DB_PORT}.
       That is probably a MySQL of your own. Either stop it, or use it:
           export OPENRATE_DB_MODE=local"
    fi
    say "    Creating container '${DB_CONTAINER}' from ${DB_IMAGE}."
    docker run --name "$DB_CONTAINER" \
      -e MYSQL_ROOT_PASSWORD="$DB_PASS" \
      -e MYSQL_DATABASE="$DB_NAME" \
      -p "${DB_PORT}:3306" \
      -d "$DB_IMAGE" >/dev/null
  fi

  # The image initialises itself on first run, which takes a while and
  # involves a restart, so wait for the server to answer and stay answering.
  printf '    Waiting for MySQL to accept connections'
  local i settled=0
  for i in $(seq 1 90); do
    if port_open "$DB_HOST" "$DB_PORT" && db_answering; then
      settled=$((settled + 1))
      if [ "$settled" -ge 3 ]; then
        printf ' ok\n'
        return
      fi
    else
      settled=0
    fi
    db_container_running || { printf '\n'; docker logs --tail 20 "$DB_CONTAINER"; fail "The container stopped during startup."; }
    printf '.'
    sleep 1
  done
  printf '\n'
  fail "MySQL did not come up within 90s. Try:  docker logs ${DB_CONTAINER}"
}

db_load() {
  [ -f "$SCHEMA" ]     || fail "Missing ${SCHEMA}"
  [ -f "$PROCEDURES" ] || fail "Missing ${PROCEDURES}"

  say "    Creating ${DB_NAME} if it does not exist."
  printf 'CREATE DATABASE IF NOT EXISTS %s;\n' "$DB_NAME" | db_mysql

  say "    Loading the schema and reference data ($(wc -l < "$SCHEMA" | tr -d ' ') lines)."
  db_mysql "$DB_NAME" < "$SCHEMA"

  say "    Loading the stored procedures."
  db_mysql "$DB_NAME" < "$PROCEDURES"

  local zones rates
  zones=$(printf 'select count(*) from ZONE_MODEL;\n' | db_mysql --silent --skip-column-names "$DB_NAME")
  rates=$(printf 'select count(*) from PRICE_MODEL;\n' | db_mysql --silent --skip-column-names "$DB_NAME")
  say "    Loaded: ${zones} zone model rows, ${rates} price model rows."
}

case "${1:-start}" in
  start)
    check_db_tooling
    step "Starting MySQL"
    db_start
    if db_loaded; then
      say "    ${DB_NAME} already holds its reference data - nothing to load."
    else
      step "Loading ${DB_NAME}"
      db_load
    fi
    say ""
    say "Database ready. Next:  ./build.sh  then  ./run.sh"
    ;;

  load)
    check_db_tooling
    db_answering || fail "No MySQL answering on ${DB_HOST}:${DB_PORT}. Run ./db.sh start first."
    step "Loading ${DB_NAME}"
    db_load
    ;;

  status)
    check_db_tooling
    if [ "$DB_MODE" = docker ] && ! db_container_running; then
      say "Container '${DB_CONTAINER}': not running"
    elif ! db_answering; then
      say "MySQL on ${DB_HOST}:${DB_PORT}: not answering"
    elif db_loaded; then
      say "MySQL on ${DB_HOST}:${DB_PORT}: up, ${DB_NAME} loaded"
      printf 'select count(*) as ZONE_MODEL from ZONE_MODEL;\n' | db_mysql --table "$DB_NAME"
      printf 'select PARTNER, BILLING_MONTH, SCENARIO, CDR_COUNT, SECOND_COUNT, round(COST,2) as COST from INTERCONNECT_BILLING_RESULTS order by PARTNER, BILLING_MONTH;\n' \
        | db_mysql --table "$DB_NAME"
    else
      say "MySQL on ${DB_HOST}:${DB_PORT}: up, but ${DB_NAME} is empty. Run ./db.sh load"
    fi
    ;;

  shell)
    check_db_tooling
    if [ "$DB_MODE" = docker ]; then
      exec docker exec -it "$DB_CONTAINER" \
        mysql --user="$DB_USER" --password="$DB_PASS" "$DB_NAME"
    else
      exec mysql --host="$DB_HOST" --port="$DB_PORT" \
                 --user="$DB_USER" --password="$DB_PASS" "$DB_NAME"
    fi
    ;;

  stop)
    check_db_tooling
    if [ "$DB_MODE" = local ]; then
      say "OPENRATE_DB_MODE is 'local' - this script does not stop your own server."
      exit 0
    fi
    if db_container_running; then
      step "Stopping '${DB_CONTAINER}'"
      docker stop "$DB_CONTAINER" >/dev/null
      say "Stopped. The data is still there - ./db.sh start brings it back."
    else
      say "Container '${DB_CONTAINER}' is not running."
    fi
    ;;

  reset)
    check_db_tooling
    if [ "$DB_MODE" = local ]; then
      step "Dropping and reloading ${DB_NAME}"
      printf 'DROP DATABASE IF EXISTS %s;\n' "$DB_NAME" | db_mysql
      db_load
    else
      step "Removing '${DB_CONTAINER}' and starting over"
      docker rm -f "$DB_CONTAINER" >/dev/null 2>&1 || true
      db_start
      db_load
    fi
    say ""
    say "Database reset."
    ;;

  *)
    fail "Usage: $0 [start|load|status|shell|stop|reset]"
    ;;
esac
