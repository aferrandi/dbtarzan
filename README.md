DbTarzan
========

To explore the tables of a database following the relations as defined in their foreign keys.


![DbTarzan](doc/window.jpeg?raw=true)

Foreign keys are  used as constraints, but they contain information about the relationships among tables. 
DbTarzan uses this information to traverse the tables of a database.

More details in the wiki.

It possible to explore the parts of database related to specific items in a table. 

Code
----

DBTarzan is written in Scala, uses JavaFX via ScalaFX for its GUI, uses the actors of Akka, Spray 
(now part of Akka) to read the configurations written in JSON.

