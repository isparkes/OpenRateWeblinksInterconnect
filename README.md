Weblinks interconnect example

This is a decommisioned live implementation from the defunct Weblinks mobile operator.

In order to run this project you need to follow some steps:

1) Download and build OpenRate. Maven should install the OpenRate jar file into your local repository
2) You need have have a MySQL database set up, and need to load the configuration data into the database, and also the stored procedure

mysqladmin --user=root --password=<password> create WeblinksInterconnectDB
mysql --user=root --password=<password> WeblinksInterconnectDB < WeblinksInterconnectDB_Full.sql (uncompress it first)
mysql --user=root --password=<password> WeblinksInterconnectDB < ConfigData/WeblinksInterconnect/SP-Procedure-Create.sql



