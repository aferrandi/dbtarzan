DbTarzan installation
=====================

dbtarzan_XXX.exe : executable for Windows. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_assembly.jar : executable jar for Linux, MacOSX and other OSes. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_all.deb : Debian pacakge for Linux. Download it and install it as a debian package, normally it is enough to double-click on the .deb file from the file manager. It is kind of experimental, because it can't be executed from the system menu, but it starts by typing "dbtarzan" in the shell; and because the debian installer sees it as "non free software", even if it is open source.     


DBTarzan needs Java 8, but in case of the Windows installation and of the debian package it should get installed automatically.

DBTarzan changelog
=================

- Release 1.16

A text box on the top of the table list allows to filter them, so that only the ones with names or columns containing the text are displayed.

If the where clause written in the "Where" text box is wrong, running the query turns the text color to red.

The database list is now sorted, making it easy to find the correct database with many databases available.

When no table is found in the database, assuming the wrong schema was chosen, the error message shows a list of the available schemas.

Pressing the "help" menu shows the now the correct documentation page https://aferrandi.github.io/dbtarzan/ .

- Release 1.15

show if the columns are part of primary or foreign keys with icons of keys.

centralized the handling of combination keys (e.g. Ctrl+Shift+...) because the accelerators did not work for menus under tabs

when the table names are loaded after the connection to a database, show it so that if none was found the user knows it and can decide what to do 
about it

sort of the table names list so that it is easier to find them when there are many

