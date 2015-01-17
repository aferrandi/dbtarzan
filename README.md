DbTarzan
========

To explore the tables of a relational database following the relations as defined in their foreign keys.


![DbTarzan](doc/window.jpeg?raw=true)

Foreign keys are  used as constraints, but they contain information about the relationships among tables. 
DbTarzan uses this information to traverse the tables of a database.

More details in the [Wiki](https://github.com/aferrandi/dbtarzan/wiki).

Examples
--------

Bank: find fast who is the owner of a contract, its coupons, its portfolio.

Hospital: find fast all the people that got a special kind of sickness.

You can do the same with standard SQL queries, but in this way it is much faster.

Code
----

DBTarzan is written in Scala, uses JavaFX 8 via ScalaFX for its GUI, uses the actors of Akka, Spray 
(now part of Akka) to read the configurations written in JSON. 

