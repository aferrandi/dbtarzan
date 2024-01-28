DbTarzan installation
=====================

dbtarzan_XXX.exe : executable for Windows. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_assembly_YYY.jar : (where YYY is linux, win or mac) executable jar for Linux, MacOSX and other OSes. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_all.deb : Debian pacakge for Linux. Download it and install it as a debian package, normally it is enough to double-click on the .deb file from the file manager. It is kind of experimental, because it can't be executed from the system menu, but it starts by typing "dbtarzan" in the shell; and because the debian installer sees it as "non free software", even if it is open source.     


DBTarzan changelog
=================
- Release 1.32

Check box in the composite editor to decide for each composite if the databases in the composite will be part of the database lists in the main UI. In this way if the database list is too big we can reduce it.

Button bar instead of menu over the tables list in the main UI, to make the "Reconnect" button visible.

- Release 1.31

Allows to have databases without storing their passwords in the connections configuration file, useful for example for production databases. The user enters the password at the moment he connects to the database.

Better error message dialog with more concise error message with an "advanced" button, that shows the whole stack trace.

Fixed a bug that did not allow to see the content of a table with a binary field with null values.

Fixed a bug that was limiting the tables that could be selected when adding an virtual foreign key.








