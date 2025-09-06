DbTarzan installation
=====================

dbtarzan_XXX.exe : executable for Windows. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_assembly_YYY.jar : (where YYY is linux, win or mac) executable jar for Linux, MacOSX and other OSes. Just copy it to a directory and run it. dbtarzan needs a [connections.config file](https://aferrandi.github.io/dbtarzan/The-database-connections-configuration-file) in the same directory. 

dbtarzan_XXX_all.deb : Debian pacakge for Linux. Download it and install it as a debian package, normally it is enough to double-click on the .deb file from the file manager. It is kind of experimental, because it can't be executed from the system menu, but it starts by typing "dbtarzan" in the shell; and because the debian installer sees it as "non free software", even if it is open source.     


DBTarzan changelog
=================
- Release 1.36

use LEFT functions on large strings in the SQL queries to improve performance and prevent timeouts. Specify the LEFT function to use for the database in the connection configuration to do this.

the foreign keys list becomes a grid with the most important columns (table to, columns from, columns to) and a text box showing all the information related to a single foreign key (included the number of tows which will be displayed following it, if desired)

radio button under the foreign key list to show the number of rows that will be displayed if you follow one foreign key. When exploring databases with many foreign keys it is useful to know which foreign keys is meaningful to follow (not the ones with 0 rows).

adds an icon and a text to the unique indexes in the indexes info view, so that it is clear which indexes are unique and which are not.

**WINDOWS: not working existing database connections can be fixed reloading the JDBC driver JAR file in the connection editor.**

- Release 1.35

removed the possibility to have non encrypted passwords in the connection config file.

possibility to use IN clauses instead of ORs in the queries resulting from tables traversal using foreign keys.










