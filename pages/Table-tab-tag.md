---

layout: default
title: Table tab tag 
resource: true
categories: [GUI]

---

## Table tab tag

Each tab added by opening a table or following a relation has a tag, which is the following:
* The name of the **table**
* If the table has been opened from another table (original table) using a **foreign key**, it is in the format **[TABLE] < [ORIGINAL TABLE]**. 
* If the tab is the result of a filter in the **where** text box in another tab, the tag last character is a **star** (*).

Examples:

CITIES  
the tab shows the table CITIES

CITIES < COUNTRIES  
the tab shows the rows of the table CITIES resulting from clicking on the foreign key that connects COUNTRIES to CITIES

CITIES *  
the tab shows the rows in the table CITIES that result from filtering the rows of the table with a where clause.
