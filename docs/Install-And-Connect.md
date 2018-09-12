---

layout: default
title: Install and connect
resource: true
categories: [Other]

---

## Install and connect

To install DBTarzan is very easy, since it is one executable file. 
Download 
- **dbTarzan_XXX.exe** for Windows. Create a directory and move the downloaded file to it. 
- **dbTarzan_X.XX_all.deb** for debian based Linuxes. 
- **dbtarzan-XXX-assembly.jar** file for the other OSes. Create a directory and move the downloaded file to it. 
- **DBTarzan-xxx.app.zip** for MacOS. The file is not signed, therefore you need to confirm you want to open it. Move the downloaded file to the directory where it is supposed to be executed; in the finder open the context menu with the right mouse button and press **Open**. In the dialog that appears confirm that you want to open the file. After this it will be possible to open it just by double-clicking on it. This installation does not include a Java runtime, therefore you need to [install](https://www.java.com/en/download/manual.jsp) it yourself,

It is also possible to install it as a [snap](https://snapcraft.io/dbtarzan).


DBTarzan needs [Java 8](http://java.com/en/download/) to run. The Windows and Linux installers should install it if it is not already installed. 
DBTarzan works in these [environments](Tested-databases-and-operating-systems).

To connect to a database:

* Run the executable file. A double-click is good enough.
* Download the [JDBC driver](http://www.sql-workbench.net/manual/jdbc-setup.html#jdbc-drivers) for the database you want to connect to.
* Use the [connection editor](Connections-editor) to describe how DBTarzan can connect to the database.Here you need to specify the [standard JDBC configuration](https://vladmihalcea.com/jdbc-driver-connection-url-strings/): location of the driver on the disk, url, userid, password.
* Connect to your database and start to [explore it](Usage)

In case of problems connecting to the database, read the [troubleshooting guide](Troubleshooting).
