---

layout: default
title: Tested DB and OS
resource: true
categories: [Other]

---

## Tested databases and operating systems

DBTarzan uses [JDBC](https://en.wikipedia.org/wiki/Java_Database_Connectivity) to connect to databases, therefore it should work with all databases that have a JDBC driver.

This is a list of the databases for which DbTarzan has been tested and it is reported working with at least one standard database with some tables and  [foreign keys](ForeignKeys):

* Oracle
* SQL Server
* Derby
* SQLite
* MySQL
* PostgreSQL
* H2

Check the [sample configuration file](https://github.com/aferrandi/dbtarzan/blob/master/connections.config) to connect to them.

This a list of the operating systems on which DbTarzan has been tested:

* Windows
* Ubuntu
* MacOS

There is an [installer](Installation) for each system.