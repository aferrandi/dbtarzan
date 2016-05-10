DbTarzan
========

[![Join the chat at https://gitter.im/aferrandi/dbtarzan](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/aferrandi/dbtarzan?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

To explore the tables of a relational database following the relations defined in their foreign keys.


![DbTarzan](doc/window.jpeg?raw=true)

Foreign keys are  used as constraints, but they contain information about the relationships among tables. 
DbTarzan uses this information to traverse the tables of a database.

The following video shows an example of database exploration:

[![IMAGE dbtarzan video on youtube](http://img.youtube.com/vi/-hR9ZLf3bNY/0.jpg)](https://youtu.be/-hR9ZLf3bNY)

More details in the [Wiki](https://github.com/aferrandi/dbtarzan/wiki).

Examples
--------

Bank: find fast who is the owner of a contract, its coupons, its portfolio.

Hospital: find fast all the people that got a special kind of sickness.

You can do the same with standard SQL queries or with views, but in this way it is much faster.
This is for example the best tool to explore a database you never saw before.

Code
----

DBTarzan is written in Scala, uses JavaFX 8 via ScalaFX for its GUI, uses the actors of Akka, Spray 
(now part of Akka) to read the configurations written in JSON. 

