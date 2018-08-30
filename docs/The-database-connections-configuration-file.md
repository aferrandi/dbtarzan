---

layout: default
title: Configuration file
resource: true
categories: [Other]

---

## The database connections configuration file

DBTarzan uses the configuration file **connections.config** to connect to databases.
The file contains a list of databases to connect to; for each database it specifies the JDBC parameters used to connect to the database.
The file is in JSON format.
An example of the file can be found [here](https://github.com/aferrandi/dbtarzan/blob/master/connections.config).
The structure of the file is a JSON vector of the following type of structure:

> {  
>  "jar": "[the driver's jar file]",  
>  "name": "[name of the database, as displayed in the dbtarzan database list]",    
>  "url": "[JDBC url to the database]",  
>  "driver": "[java class package+name to of the JDBC driver]",  
>  "user": "[user id to connect to the database]",  
>  "password": "[password to connect to the database]"  
>  "schema": "[the schema containing the data in the database (optional/used for example in Oracle)]"  
>  "instances": [the number of connections/thread for this database. Can be useful to have 2 connections to the same database if the access to the database is slow, to query tables faster (the query to load the rows and the one to load the foreign keys can go in parallel)] 
>
> }  

The schema is absolutely needed for Oracle, otherwise DbTarzan will show all tables of all schemas, including the system tables. The schema (user) must be written with uppercase letters.

From version 0.97 the file can be created or edited directly inside DbTarzan, clicking on the <b>Edit Connections</b> context menu of the databases list:

![Edit Connections](images/buildForeignKeysFile.jpeg)

and then using the [connections editor](Connections-editor):


