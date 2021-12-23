---

layout: default
title: Is it safe?
resource: true
categories: [Other]

---

## Can I use it with production databases?

DBTarzan does not change the content of the databases, it uses only **read-only** queries, so it can't change the content of the database:
* To query the **names of the tables** it uses the JDBC Connection.getMetaData().getTables() method.
* To query the **columns of each table** it uses the JDBC Connection.getMetaData().getColumns() method.
* To query the **foreign keys** it uses the JDBC Connection.getMetaData().getImportedKeys() and Connection.getMetaData().getExportedKeys() methods.
* Simple **selects** with simple where clauses (on the primary key or on the foreign keys) are used to show the **content of the tables**.   

DBTarzan is also built to have **good performances** and **not block** the database when reading from tables with a big number of rows or with big blob fields:
* By default DBTarzan **limits** the rows resulting from a select query to 500. This can be changed in the [Connections editor](Connections-editor), but only by purpose.
* DBTarzan can be configured in the in the [Connections editor](Connections-editor) to limit the **maximum amount of bytes** it reads from each field when showing the table rows.  When showing an individual row with the [Vertical row view](Vertical-row-view) the fields are displayed in their entirety.


I use it frequently in my office on production databases.

