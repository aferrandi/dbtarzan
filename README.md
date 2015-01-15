dbtarzan
========

To explore the tables of a database following their relations as defined in their foreign keys.

![DbTarzan](doc/window.jpeg?raw=true)

If the database foreign keys are well defined and cover all tables in the database, they can 
be used not only as constraints, but also to know which relations connect the tables in the database.

DBTarzan is a graphical application to explore databases. After you open a database, you move from 
table to table using the foreign keys that connect them.

More in detail:
You open a table, you select some rows (check boxes), you double-click on a foreign key: the connected
table opens, displaying only the row that are connected to the selected rows by the foreign key.

DBTarzan makes it possible to explore the parts of database related to specific items in a table. 

DBTarzan is written in Scala, uses JavaFX via ScalaFX for its GUI, uses the actors of Akka, Spray 
(now part of Akka) to read the configurations written in JSON.

