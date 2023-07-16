---

layout: default
title: Composite - multiple databases
resource: true
categories: [Structure]

---

## Composite - multiple databases

There are cases in which **multiple databases** are **logically connected**. 
For example the *orders* are in a database, but the descriptions of the *products* referred by these orders are in a *different* database.

The tables can be in different databases because they are accessed by different **microservices** or simply because they are accessed by different systems.

We know that DBTrarzan is a database browser that uses foreign keys to **traverse** the tables of a relational database.
But it can also traverse the tables of these *logically connected* databases.

![Composites relations](images/composites.png)

To do that:

* we define a [composite](Compositest-editor), which is a set of databases
* we connect the tables of the databases via [additional foreign keys](AdditionalForeignKeys), which are abstract foreign keys defined in DBTarzan, not in the databases


