---

layout: default
title: Connections editor
resource: true
categories: [GUI]

---

## Connections editor

DbTarzan includes a connection editor. With it it is possible to add, update and remove connections to databases.
The editor actually just changes the [connections.config file](The-database-connections-configuration-file), which is used by dbtarzan to display the list of available database connections.

![Connections editor](images/editConnections.png)

To open the editor, in the "Settings" menu on the top choose "Edit Connections".
The editor displays on the right side a list of database connections. 
Once a connection is chosen on the list, the right side of the editor shows the definition of the connection, in terms of:
* **Name**, the connection identifier
* **Jar**, the jar file with the JDBC driver used to connect to the database.
* **Url**, the JDBC url used to connect to the specific database
* **Driver**, the driver class used to connect to the database, included in the jar file.
* **User and password**, difining the login account to connect to the database. 
* **Schema**. For databases that require the indication of a schema when connecting to them, like for example Oracle and SQL server.

And optionally (generally not needed):
* **Delimiters**. If the database contains tables or columns that have as name reserved words in the database (like GROUP or RULE), queries don't work unless these names are delimited with special characters. These characters vary between databases, but they are either double quotes or squared brackets. 
* **Query timeout in seconds**: for each query (a new tab) sets a maximum limit to the query duration in seconds. This timeout acts both in the query execution itself and in loading the query result. If not specified, this timeout is 10 seconds.
* **Max rows**. The number of rows resulting from queries are limited to 500 by default: you don't want to use local memory, network and database resources to read many table rows, not to mention the queries performance. If you want to see other rows, it is generally enough to change the query parameters or the order of the resulting rows. But there are some cases in which you want to have a larger rows number limit. 
 * **Catalog**. similar concept to the schema, used to identify a specific database, added to solve issue [#78](https://github.com/aferrandi/dbtarzan/issues/78).


To **update** the definition of a database connection:
* choose it in the list
* edit its definition on the left
* click on the **Save** button. 

To **add** a new connection, click on the **New** button. A new connection is added to the list, with name <NEW> and the other fields empty.
The new definition is immediately selected, showing its definition on the left. 
The only field with some content is the Name, showing the <NEW> name. <NEW> is just a temporary name that needs to be changed. 
Change the name of the connection and fill the other field, then click on **Save**. The connection is added to the list and immediately displayed on the main window.

Connections can be **tested** directly in this editor, selecting a connection from the list and clicking on the **Test** button, which displays a success message if it wes able to connect to the database, a detailed error in the case it was not.

To **remove** a connection, select it and click on the button **Remove**. The connection disappears from the list. Clicking on Save, the file is updated and reloaded in the main window without the removed connection. 

It is also possible to **duplicate** a connection (button **Duplicate**), because in real life connection definitions tend to be similar (same server, same database with different user, same driver). The new connection will have the same definition as the original but no name.


