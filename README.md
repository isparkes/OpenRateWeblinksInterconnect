OpenRateWeblinksInterconnect
============================

Interconnect rating for a mobile operator, built on
[OpenRate](https://github.com/isparkes/OpenRate). It takes call detail records
from the MSC, works out who the call was handed to and from, rates it against
the interconnect agreement for that destination, and aggregates the result
into an interconnect bill.

This is a decommissioned live implementation from the defunct Weblinks mobile
operator, with its real reference data and four months of its real traffic.
It is a step up from [OpenRateSimple](https://github.com/isparkes/OpenRateSimple):
two pipelines, a database, and rating rather than enrichment.

Requires OpenRate 1.5.2.6 or later, JDK 17, and Docker (for the database, see
below).

Quick start
-----------

```bash
./demo.sh
```

That brings up MySQL, loads the rating tables, builds the application, starts
the framework, rates an hour of call records, shows the bill that came out,
and shuts down again.

If OpenRate itself is not installed yet, `demo.sh` stops and tells you how.
OpenRate is not published to Maven Central, so the framework has to be built
locally first — once, before any application will resolve it:

```bash
git clone https://github.com/isparkes/OpenRate.git
cd OpenRate
./bootstrap.sh
```

The database
------------

Unlike OpenRateSimple, this application keeps its rating tables, its duplicate
check and its billing results in MySQL. `db.sh` runs one in a Docker container
and loads the schema, so there is nothing to install beyond Docker:

| Command | Does |
|---|---|
| `./db.sh` | Start MySQL and load the schema and reference data |
| `./db.sh status` | Say whether it is up, and print the billing results |
| `./db.sh shell` | Open a mysql prompt on the application database |
| `./db.sh stop` | Stop the database, keeping its data |
| `./db.sh reset` | Throw it away and start over |

To use a MySQL you already run instead:

```bash
export OPENRATE_DB_MODE=local
export OPENRATE_DB_USER=root OPENRATE_DB_PASS=yourpassword
./db.sh load
```

The connection details are in `src/main/resources/WeblinksInterconnect.properties.xml`,
so a server on another host or port needs that file changed to match.

Running it yourself
-------------------

`demo.sh` just chains the other scripts together. To drive it by hand:

| Script | Does |
|---|---|
| `./build.sh` | Build the application |
| `./run.sh` | Start the framework in the foreground; runs until stopped |
| `./feed.sh` | Rate one hour of call records |
| `./feed.sh 24` | Rate a day |
| `./feed.sh all` | Rate everything in the archive (2593 files, ~4 months) |
| `./stop.sh` | Shut it down cleanly |

OpenRate runs here as a **daemon**: it starts, watches
`Data/WeblinksInterconnect` for files matching `cdr*.csv`, and keeps running
until told to stop. So `run.sh` occupies a terminal, and you drive it from a
second one:

```bash
./db.sh                  # once
./run.sh                 # terminal 1 - wait for "Running..."
./feed.sh                # terminal 2
./db.sh status           # terminal 2 - the bill
./stop.sh                # terminal 2
```

The samples live compressed in
`Data/WeblinksInterconnect/CDRsFromMSC.tar.bz2`, one file per hour from
February to May 2008. `feed.sh` extracts as many as you ask for; dropping a
file into the directory is what triggers rating.

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
partner, billing month and scenario. Feeding more files accumulates into the
same rows.

How it works
------------

Two pipelines, both defined in
`src/main/resources/WeblinksInterconnect.properties.xml`.

**WIPipeline** reads the CDRs and rates them:

| Module | Role |
|---|---|
| `WIInputAdapter` | Reads the MSC records and maps them into a `WIRecord` |
| `Normalisation` | Normalises the dialled number (regex match) |
| `TrunkLookup` → `OperatorLookup` → `TrunkICLookup` | Works out the incoming and outgoing trunk, the operator behind it, and the interconnect scenario |
| `ZoneModelValidity` → `ZoneLookup` | Picks the zone model valid on the call date, then the destination zone by longest prefix match |
| `RateLookup` | Rates the call with RUM based rating against the zone's price model |
| `DupCheck` | Rejects records already seen, from a persistent duplicate cache |
| `AggregationFilter` → `Aggregation` | Decides what is billable and accumulates it by partner, month and scenario |
| `WIOutputAdapter` | Writes each record to the stream chosen by the splitting map |

**AggLoaderPipeline** picks up the aggregation results and writes them to the
database through the `sp_UpdateAggResult` stored procedure.

All the reference data — normalisation rules, trunk and operator maps, zone
models, price models, splitting rules — is in the database, loaded into
in-memory caches at startup and reloadable from the admin console while the
framework runs.

The admin console
-----------------

OpenRate exposes a control interface on port 8086:

```bash
telnet localhost 8086
```

`Help` lists the available commands. `stop.sh` uses this to issue:

```
Framework:shutdown=true
```

> The console is unauthenticated. It is fine on a development machine; do not
> expose the port on a shared or public network.

Building a distribution
-----------------------

```bash
mvn clean package
```

also produces `target/OpenRateWeblinksInterconnect-1.0.0-bin.zip`: a
self-contained distribution that runs anywhere with a JDK 17 and Docker, with
no Maven and no source checkout.

```
unzip OpenRateWeblinksInterconnect-1.0.0-bin.zip -d weblinks
cd weblinks
bin/db.sh && bin/run.sh
```

It carries its own `README.md`, the framework and dependency jars, the
configuration, the database schema, two sample hours of traffic, and
`bin/db.sh`, `bin/run.sh`, `bin/feed.sh`, `bin/stop.sh`. Those distribution
scripts launch Java directly; the scripts in the project root drive Maven, so
each set only works in its own context. The distribution sources live in
`src/main/bin` and `src/main/dist`.

Where to go next
----------------

Read `WeblinksInterconnect.properties.xml` alongside the architecture section
of the [OpenRate README](https://github.com/isparkes/OpenRate) — the
configuration maps directly onto the pipeline diagram there.

Then try changing something: add a prefix to `ZONE_MODEL`, change a tariff in
`PRICE_MODEL`, reload the cache from the admin console, and feed the same hour
again to see the bill change without restarting anything.

Simpler to start with: [OpenRateSimple](https://github.com/isparkes/OpenRateSimple).
Other examples: [ORPixip](https://github.com/isparkes/ORPixip) (revenue
assurance across five data sources), [ORTyfon](https://github.com/isparkes/ORTyfon)
(fixed line and VoIP rating with fraud detection).

License
-------

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE).

Copyright 2006-2026 Ian Sparkes.
