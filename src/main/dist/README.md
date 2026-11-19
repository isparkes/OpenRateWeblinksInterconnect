OpenRateWeblinksInterconnect
============================

A self-contained distribution of the Weblinks interconnect rating
application, built on [OpenRate](https://github.com/isparkes/OpenRate).

It rates mobile call detail records against interconnect agreements: it
normalises the dialled number, works out which operator the call came in from
and went out to, looks up the destination zone, rates the call, checks for
duplicates, and splits the result into per-scenario output streams. A second
pipeline then aggregates the rated traffic into an interconnect bill.

This is a decommissioned live implementation from the defunct Weblinks mobile
operator, with its real reference data and a sample of its real traffic.

What you need
-------------

* A JDK, 17 or later. Nothing else from the Java side - the framework and all
  its dependencies are in this distribution.
* MySQL, for the rating tables and the billing results. `bin/db.sh` runs one
  in Docker for you, so Docker is enough; if you already run MySQL, see
  "Using your own MySQL" below.

Quick start
-----------

```bash
bin/db.sh            # start MySQL and load the schema and reference data
bin/run.sh           # start the framework; it runs until stopped
```

then, from a second terminal in the same directory:

```bash
bin/feed.sh          # rate a sample hour of call records
bin/db.sh status     # show the interconnect bill that came out
bin/stop.sh          # shut the framework down
bin/db.sh stop       # shut the database down
```

The scripts
-----------

| Script | Does |
|---|---|
| `bin/db.sh` | Start, load, inspect, stop or reset the database |
| `bin/run.sh` | Start the framework in the foreground |
| `bin/feed.sh` | Feed it the small sample (168 records) |
| `bin/feed.sh large` | Feed it the large sample (~3000 records) |
| `bin/stop.sh` | Shut it down cleanly through the admin console |

OpenRate runs here as a **daemon**: it starts, watches
`Data/WeblinksInterconnect` for files matching `cdr*.csv`, and keeps running
until told to stop. Dropping a file in is what triggers rating; there is
nothing else to press.

What comes out
--------------

Rated records are written to a directory chosen by the interconnect scenario:

| Directory | Contents |
|---|---|
| `Done/` | The input file, renamed once it has been consumed |
| `InterconnectBilling/` | Calls billable to an interconnect partner |
| `InterconnectReconciliation/` | Calls reconciled against a partner's own records |
| `WeblinksTraffic/` | Traffic that stayed on the Weblinks network |
| `Filter/` | Calls filtered out before billing |
| `Duplicate/` | Calls already seen, caught by the duplicate check |
| `Error/` | Calls that could not be rated |
| `Dump/` | A human readable dump of each record's processing |

The aggregated bill goes to the `INTERCONNECT_BILLING_RESULTS` table, keyed on
partner, billing month and scenario, through a stored procedure. Feeding more
files accumulates into the same rows. `bin/db.sh status` prints the table.

What is in here
---------------

| Directory | Contents |
|---|---|
| `bin/` | These scripts |
| `properties/` | The pipeline configuration and the logging configuration |
| `dist/` | This application |
| `ORlib/` | The OpenRate framework |
| `lib/` | Third party dependencies |
| `db/` | The database schema, reference data and stored procedures |
| `samples/` | Sample call records, gzipped |
| `ConfigData/` | The aggregation definitions |
| `Data/` | Where input and output files live |
| `log/` | Framework, pipeline, statistics and error logs |

The admin console
-----------------

OpenRate exposes a control interface on port 8086:

```bash
telnet localhost 8086
```

`Help` lists the available commands. `bin/stop.sh` uses this to issue
`Framework:shutdown=true`.

> The console is unauthenticated. It is fine on a development machine; do not
> expose the port on a shared or public network.

Using your own MySQL
--------------------

```bash
export OPENRATE_DB_MODE=local
export OPENRATE_DB_USER=root OPENRATE_DB_PASS=yourpassword
bin/db.sh load
```

The connection details are hard coded in
`properties/WeblinksInterconnect.properties.xml`, so a server on another host
or port needs that file changed to match.

License
-------

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE).

Copyright 2006-2026 Ian Sparkes.
