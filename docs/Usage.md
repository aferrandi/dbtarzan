---

layout: default
title: Usage
resource: true
categories: [GUI]

---

## Usage

Open a database from the list (read from the [Configuration](./The-database-connections-configuration-file)), double-clicking on it.
If the database list is empty, add a database with the Connections Editor.
This example uses the Derby demo database, ToursDB.

![Databases](images/databases.png)

You get a list of the tables contained in the database:

![Tables](images/tables.png)

The list of tables can be [filtered](./Tables-Names-Filter). Open a table double-clicking on it:

![Selection](images/selection.png)

If you select some rows (like in the previous image) and double-click on a foreign key, you open the table related to the original one through the foreign key, showing the rows related to the originally selected rows:

![Derived](images/derived.png)

You can select specific rows of the table specifying a where clause:

![Where](images/where.png)

DbTarzan shows the original rows in the table filtered with the where clause: 

![Where Result](images/whereresult.png)

Every time you follow a foreign key or filter the table with a where clause DbTarzan opens a new tab leaving the original one immutated.