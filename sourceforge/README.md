DbTarzan installation
=====================

dbtarzan_XXX.exe : executable for Windows. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://github.com/aferrandi/dbtarzan/wiki/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_assembly.jar : executable jar for Linux, MacOSX and other OSes. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://github.com/aferrandi/dbtarzan/wiki/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_all.deb : Debian pacakge for Linux. Download it and install it as a debian package, normally it is enough to double-click on the .deb file from the file manager. It is kind of experimental, because it can't be executed from the system menu, but it starts by typing "dbtarzan" in the shell; and because the debian installer sees it as "non free software", even if it is open source.     


DBTarzan needs Java 8, but in case of the Windows installation and of the debian package it should get installed automatically.

DBTarzan changelog
=================

- Release 1.13

Added view with vertical representation of a row.

More checks in the order by dialog box.

Copy to clipboard table rows with or without the table header 

Accelerators for the menu items of the table menu

- Release 1.12

Converted the context menus on the top of the "Tables" and "Table" panel to standard menus.

Added time to the log lines in the log panel.

