DbTarzan installation
=====================

dbtarzan_XXX.exe : executable for Windows. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://github.com/aferrandi/dbtarzan/wiki/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_one-jar.jar : executable jar for Linux, MacOSX and other OSes. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://github.com/aferrandi/dbtarzan/wiki/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_all.deb : Debian pacakge for Linux. Download it and install it as a debian package, normally it is enough to double-click on the .deb file from the file manager. It is kind of experimental, because it can't be executed from the system menu, but it starts by typing "dbtarzan" in the shell; and because the debian installer sees it as "non free software", even if it is open source.     


DBTarzan needs Java 8, but in case of the Windows installation and of the debian package it should get installed automatically.

DBTarzan changelog
=================

- Release 1.06

Some databases close automatically a connection after a period of inactivity. Added a context menu to reset a connection so that it is not needed to open a new database tab in this case. 

- Release 1.05

Now it is possible to configure a connection such that all identifiers (table names + column names) are delimited by special delimiters ([ and ] or " and "), so that sql keys can be used as identifiers.

- Release 1.04

It is possible to close all table tabs, close all table tabs before the current tab.

If a table has only one row, that row is automatically selected
