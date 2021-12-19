DbTarzan installation
=====================

dbtarzan_XXX.exe : executable for Windows. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_assembly_YYY.jar : (where YYY is linux, win or mac) executable jar for Linux, MacOSX and other OSes. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_all.deb : Debian pacakge for Linux. Download it and install it as a debian package, normally it is enough to double-click on the .deb file from the file manager. It is kind of experimental, because it can't be executed from the system menu, but it starts by typing "dbtarzan" in the shell; and because the debian installer sees it as "non free software", even if it is open source.     


DBTarzan changelog
=================

- Release 1.26

show foreign keys earlier, making it possible to follow the foreign keys of a row without waiting all rows to be loaded.

double-clicking on a row or pressing enter with the row selected shows the vertical row panel with all the data of the row

does only allow to store connections with identifiers as names (not <NEW> for example)

- Release 1.25

In the info tab there is a third panel showing the indexes of the table.

The order by dialog works now in the same way as the columns chooser in Additional foreign keys editor,

The first row of the table does not get selected by default. This happens only when the vertical row view.

Fixes an error in the release 1.24 that was not allowing opening sqllite databases.


