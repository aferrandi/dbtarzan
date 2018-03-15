DbTarzan installation
=====================

dbtarzan_XXX.exe : executable for Windows. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://github.com/aferrandi/dbtarzan/wiki/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_one-jar.jar : executable jar for Linux, MacOSX and other OSes. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://github.com/aferrandi/dbtarzan/wiki/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_all.deb : Debian pacakge for Linux. Download it and install it as a debian package, normally it is enough to double-click on the .deb file from the file manager. It is kind of experimental, because it can't be executed from the system menu, but it starts by typing "dbtarzan" in the shell; and because the debian installer sees it as "non free software", even if it is open source.     


DBTarzan needs Java 8, but in case of the Windows installation and of the debian package it should get installed automatically.

DBTarzan changelog
=================

- Release 1.09

If the connection configuration includes a schema, it gets used not only for the foreign keys but also for the tables.
Needed for SQL Server if the schema is not "dbo".

- Release 1.08

The passwords in the connections.config file are from now on encrypted by default.
For backward compatibility the existing passwords, not encrypted, are correctly read and used by dbtarzan, but reentering them and saving them in the connection editor will turn them to encrypted.

More error handling when connecting to a database.

