---

layout: default
title: Connections editor
resource: true
categories: [GUI]

---

## Connections editor

DbTarzan includes a connection editor. With it it is possible to add, update and remove connections to databases.
The editor actually just changes the [connections.config file](The-database-connections-configuration-file), which is used by dbtarzan to display the list of available database connections.

![Connections editor](/images/editConnections.jpeg)

To open the editor, in the menu on the top choose "Connections" and then "Edit Connections".
The editor displays on the right side a list of database connections. 
Once a connection is chosen on the list, the right side of the editor shows the definition of the connection, in terms of:
* **Name**, the connection identifier
* **Jar**, the jar file with the JDBC driver used to connect to the database.
* **Url**, the JDBC url used to connect to the specific database
* **Driver**, the driver class used to connect to the database, included in the jar file.
* **User and password**, difining the login account to connect to the database.
* **Schema**. For databases that require the indication of a schema when connecting to them, like for example Oracle and SQL server.
* **Delimiters**. If the database contains tables or columns that have as name reserved words in the database (like GROUP or RULE), queries don't work unless these names are delimited with special characters. These characters vary between databases, but they are either double quotes or squared brackets. 
 


To **update** the definition of a database connection:
* choose it in the list
* edit its definition on the left
* click on the **Save** button. 

To **add** a new connection, click on the **New** button. A new connection is added to the list, with name <NEW> and the other fields empty.
The new definition is immediately selected, showing its definition on the left. 
The only field with some content is the Name, showing the <NEW> name. <NEW> is just a temporary name that needs to be changed. 
Change the name of the connection and fill the other field, then click on **Save**. The connection is added to the list and immediately displayed on the main window.

To **remove** a connection, select it and click on the button **Remove**. The connection disappears from the list. Clicking on Save, the file is updated and reloaded in the main window without the removed connection. 
