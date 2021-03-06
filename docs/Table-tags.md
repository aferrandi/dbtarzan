---

layout: default
title: Table tags 
resource: true
categories: [GUI]

---

## Table tab tags

Each tab added by opening a table or following a relation has a tag, which is the following:
* The name of the **table**
* If the table has been opened from another table (original table) using a [foreign key](ForeignKeys), it is in the format **[TABLE] < [ORIGINAL TABLE]**. 
* If the tab is the result of a filter in the **where** text box in another tab, the tag last character is a **star** (*).

![Table tab tag](images/tableTabTag.png)

Examples:

CITIES  
the tab shows the table CITIES

CITIES < COUNTRIES  
the tab shows the rows of the table CITIES resulting from clicking on the foreign key that connects COUNTRIES to CITIES

CITIES *  
the tab shows the rows in the table CITIES that result from filtering the rows of the table with a where clause.

