---

layout: default
title: Is it safe?
resource: true
categories: [Other]

---

## Can I use it with production databases?

DBTarzan does not change the content of the databases, it uses only **read-only** queries:
* To query the names of the tables it uses the JDBC Connection.getMetaData().getTables() method.
* To query the columns of each table it uses the JDBC Connection.getMetaData().getColumns() method.
* To query the foreign keys it uses the JDBC Connection.getMetaData().getImportedKeys() and Connection.getMetaData().getExportedKeys() methods.
* Simple selects with simple where clauses (on the primary key or on the foreign keys) are used to show the content of the tables.   

I use it frequently in my office on production databases.

