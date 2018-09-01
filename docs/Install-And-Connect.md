---

layout: default
title: Install and connect
resource: true
categories: [Other]

---

## Install and connect

To install DBTarzan is very easy, since it is one executable file. 
Download **dbTarzan_XXX.exe**  file for Windows, **dbTarzan_X.XX_all.deb** for debian based Linuxes or the **dbtarzan-XXX-assembly.jar** file for the other OSes. Create a directory and move the downloaded file to it. It is also possible to install it as a [snap](https://snapcraft.io/dbtarzan).


DBTarzan needs [Java 8](http://java.com/en/download/) to run. The Windows and Linux installers should install it if it is not already installed. 
DBTarzan works in these [environments](Tested-databases-and-operating-systems).

To connect to a database:

* Run the executable file. A double-click is good enough.
* Download the [JDBC driver](http://www.sql-workbench.net/manual/jdbc-setup.html#jdbc-drivers) for the database you want to connect to.
* Use the [connection editor](Connections-editor) to describe how DBTarzan can connect to the database.Here you need to specify the [standard JDBC configuration](https://vladmihalcea.com/jdbc-driver-connection-url-strings/): location of the driver on the disk, url, userid, password.
* Connect to your database and start to [explore it](Usage)

In case of problems connecting to the database, read the [troubleshooting guide](Troubleshooting).
