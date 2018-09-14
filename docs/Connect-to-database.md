---

layout: default
title: Cconnect to a database
resource: true
categories: [Other]

---

## Connect to a database

DBTarzan works at least in these [environments](Tested-databases-and-operating-systems).

To connect to a database:

* Run the executable file. A double-click is good enough.
* Download the [JDBC driver](http://www.sql-workbench.net/manual/jdbc-setup.html#jdbc-drivers) for the database you want to connect to.
* Use the [connection editor](Connections-editor) to describe how DBTarzan can connect to the database.Here you need to specify the [standard JDBC configuration](https://vladmihalcea.com/jdbc-driver-connection-url-strings/): location of the driver on the disk, url, userid, password.
* Connect to your database and start to [explore it](Usage)

In case of problems connecting to the database, read the [troubleshooting guide](Troubleshooting).
