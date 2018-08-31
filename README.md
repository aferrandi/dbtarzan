DbTarzan
========

[![Join the chat at https://gitter.im/aferrandi/dbtarzan](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/aferrandi/dbtarzan?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Database browser.

Explore the tables of a relational database following the relations defined in their foreign keys.

With DbTarzan you can find the information you need from the database in a fraction of the time, without writing queries.  
If it is the first time you are looking into a database, with DbTarzan you get immediately a sense of the data in the tables and the relations among them.

![DbTarzan](docs/images/window.jpeg?raw=true)

Foreign keys are  used as constraints, but they contain information about the relationships among tables. 
DbTarzan uses this information to traverse the tables of a database.

The following videos show examples of database exploration, the first one is a simple exploration, the second uses queries and order by:

[![IMAGE dbtarzan simple video on youtube](https://img.youtube.com/vi/qLh5HnW0Rwc/default.jpg)](https://youtu.be/qLh5HnW0Rwc)

[![IMAGE dbtarzan detailed video on youtube](https://img.youtube.com/vi/CezsF9vME6U/default.jpg)](https://youtu.be/CezsF9vME6U)

More details in the [Documentation](https://aferrandi.github.io/dbtarzan).

Examples
--------

Bank: with a few mouse clicks, from a single operation, get the source and destination account, the account owners, which other operations was made in the same day between the accounts. 

Hospital: with a few mouse clicks, from a patient, find the reaason of the hospitalization, the insurance data, the clinical history, which other patients are in the hospital for the same reason.

You can do the same with standard SQL queries or with views, but in this way it is much faster.

