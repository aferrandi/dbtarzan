---

layout: default

---

## Usage

Open a database from the list (read from the [Configuration](./The-database-connections-configuration-file)), double-clicking on it.
If the database list is empty, add a database with the Connections Editor.
This example uses the Derby demo database, ToursDB.

![Databases](/images/databases.jpeg)

You get a list of the tables contained in the database:

![Databases](/images/tables.jpeg)

Open a table double-clicking on it:

![Databases](/images/selection.jpeg)

If you select some rows (like in the previous image) and double-click on a foreign key, you open the table related to the original one through the foreign key, showing the rows related to the originally selected rows:

![Databases](/images/derived.jpeg)

You can select specific rows of the table specifying a where clause:

![Databases](/images/where.jpeg)

DbTarzan shows the original rows in the table filtered with the where clause: 

![Databases](/images/whereresult.jpeg)

Every time you follow a foreign key or filter the table with a where clause DbTarzan opens a new tab leaving the original one immutated.