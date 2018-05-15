DbTarzan installation
=====================

dbtarzan_XXX.exe : executable for Windows. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://github.com/aferrandi/dbtarzan/wiki/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_assembly.jar : executable jar for Linux, MacOSX and other OSes. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://github.com/aferrandi/dbtarzan/wiki/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_all.deb : Debian pacakge for Linux. Download it and install it as a debian package, normally it is enough to double-click on the .deb file from the file manager. It is kind of experimental, because it can't be executed from the system menu, but it starts by typing "dbtarzan" in the shell; and because the debian installer sees it as "non free software", even if it is open source.     


DBTarzan needs Java 8, but in case of the Windows installation and of the debian package it should get installed automatically.

DBTarzan changelog
=================

- Release 1.11
Foreign keys list (right side): If the foreign key is of the original table, it is marked with "<", otherwise (it is of the destination table, turned) it is marked with ">".
Foreign keys list (right side): If there are two or more foreign keys to the same table, the fields used by the foreign key are displayed together with the table to distinguish them.

- Release 1.10

Added possibility to use order by clauses in the queries, selecting the columns to use as order by columns and the direction (ascending or descending).



