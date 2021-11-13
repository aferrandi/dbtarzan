---

layout: default
title: Table Information
resource: true
categories: [GUI]

---

## Table Information

When you open a table tab, DBTarzan shows the content of the table, and on the right a panel with the  [foreign keys](ForeignKeys) of that table.
Under the foreign keys table there is another panel, showing **additional table information**.
This panel is divided in the following tabs:
  
### Table Query

![Table query](images/tableQuery.png)

Shows the **query** that extracted the content displayed in the table.
It can be **copied**, just selecting it and copying the selection.
So you can for example run the query in **other applications**.

### Table Fields

![Table fields](images/tableFields.png)

Shows the structure of a table in terms of its **fields**, e.g. the name of the fields and their type.
This can be used for example to write a program that reads the content of the table; 
you need to know for example if the fields are texts of numbers, if they are nullable.

The **copy content to clipboard menu** copies the whole table structure (fields names and types) to the clipboard, in a tabular format.

### Table Indexes

![Table fields](images/tableIndexes.png)

Shows a list of the indexes of a table.
Eech index is displayed with:
* its **name**
* a list of the **fields** included in the index, each with its **direction** in the index (ascending, descending)

Other informations, for example the fields' types, can be found in the **table fields**.