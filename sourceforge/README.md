DbTarzan installation
=====================

dbtarzan_XXX.exe : executable for Windows. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_assembly.jar : executable jar for Linux, MacOSX and other OSes. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_all.deb : Debian pacakge for Linux. Download it and install it as a debian package, normally it is enough to double-click on the .deb file from the file manager. It is kind of experimental, because it can't be executed from the system menu, but it starts by typing "dbtarzan" in the shell; and because the debian installer sees it as "non free software", even if it is open source.     


DBTarzan needs Java 8, but in case of the Windows installation and of the debian package it should get installed automatically.

DBTarzan changelog
=================

- Release 1.15

show if the columns are part of primary or foreign keys with icons of keys.

centralized the handling of combination keys (e.g. Ctrl+Shift+...) because the accelerators did not work for menus under tabs

when the table names are loaded after the connection to a database, show it so that if none was found the user knows it and can decide what to do 
about it

sort of the table names list so that it is easier to find them when there are many

- Release 1.14

Installation file for MacOS (DBTarzanxxx.app.zip).

When connecting to the database, in case of failure the error is displayed correctly.

Complete error messages in case of SQL errors.

Show a link to a "JDBC connection strings" page in the connection editor.

