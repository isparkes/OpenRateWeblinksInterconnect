mysqladmin --user=root --password=cpr drop WeblinksInterconnectDB
mysqladmin --user=root --password=cpr create WeblinksInterconnectDB
mysql --user=root --password=cpr WeblinksInterconnectDB < ..\ConfigData\WeblinksInterconnect\SP-Procedure-Create.sql
mysql --user=root --password=cpr WeblinksInterconnectDB < WeblinksInterconnectDB.sql

