DbTarzan installation
=====================

dbtarzan_XXX.exe : executable for Windows. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_assembly.jar : executable jar for Linux, MacOSX and other OSes. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_all.deb : Debian pacakge for Linux. Download it and install it as a debian package, normally it is enough to double-click on the .deb file from the file manager. It is kind of experimental, because it can't be executed from the system menu, but it starts by typing "dbtarzan" in the shell; and because the debian installer sees it as "non free software", even if it is open source.     


DBTarzan needs Java 8, but in case of the Windows installation and of the debian package it should get installed automatically.

DBTarzan changelog
=================

- Release 1.19

Added the possibility to use a master password to encrypt all the databases' password in the connections.config file.

Added the possibility to use catalog (similar concept to schema) as JDBC parameter to connect to a specific database.

Keeping the Ctrl button pressed when double-clicking a foreign key it closes the previous tab when opening the new one.


- Release 1.18

Automatic sizing of the table columns to fit the columns content. This means more information in the UI.

Internationalization: Added Spanish and Italian to the set of languages used to display the application's texts, buttons and menus. Added the "global options" panel to choose the language.

Remove the progress bar from the UI after a table has been completely loaded. It is not needed and just uses space.

