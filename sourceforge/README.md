DbTarzan installation
=====================

dbtarzan_XXX.exe : executable for Windows. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://github.com/aferrandi/dbtarzan/wiki/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_one-jar.jar : executable jar for Linux, MacOSX and other OSes. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://github.com/aferrandi/dbtarzan/wiki/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_all.deb : Debian pacakge for Linux. Download it and install it as a debian package, normally it is enough to double-click on the .deb file from the file manager. It is kind of experimental, because it can't be executed from the system menu, but it starts by typing "dbtarzan" in the shell; and because the debian installer sees it as "non free software", even if it is open source.     


DBTarzan needs Java 8, but in case of the Windows installation and of the debian package it should get installed automatically.

DBTarzan changelog
=================

- Release 1.08

The passwords in the connections.config file are from now on encrypted by default.
For backward compatibility the existing passwords, not encrypted, are correctly read and used by dbtarzan, but reentering them and saving them in the connection editor will turn them to encrypted.

More error handling when connecting to a database.

- Release 1.07

Added a duplicate button in the connection editor, to reuse parts of an existing connection definition (driver path, driver class) when creating a new one.
Added a field in the connection definition, maxRows, which defines the maximum amount of rows returned by a query. If not set, it remains 500 as it was before version 1.07. You need to reopen the connection to use the new value.

