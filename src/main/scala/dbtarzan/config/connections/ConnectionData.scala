package dbtarzan.config.connections

import dbtarzan.config.password.Password
import dbtarzan.db.IdentifierDelimiters


/* JDBC configuration for a database */
case class ConnectionData(
	/* the path of the jar file of the driver */
	jar : String, 
	/* name of the database, as shown in the GUI */
	name: String, 
	/* the class name of the JDBC driver */
	driver: String, 
	/* the JDBC url used to connect*/
	url: String,
	/* the schema containing the data, in multi-schema databases (Oracle) */
	schema: Option[String],
	/* the user id to login to the database */
	user: String, 
	/* the password to login to the database */
	password: Password,
	/* if true the password is encrypter. Used to be able to edit the password in configuration file */ 
	passwordEncrypted: Option[Boolean],
	/* the number of connections that the application will open against this database (1 if not defined) */
	instances: Option[Int],
	/* the identifiers delimiters to prevent having troubles with reserved words */
	identifierDelimiters: Option[IdentifierDelimiters],
	/* the path of the jar file of the driver */
	maxRows : Option[Int],
	/* the max time a query can take in seconds */
	queryTimeoutInSeconds : Option[Int],
	/* the catalog of the database containing the data, used when the schema is not enough (table user n MySQL)  */
	catalog: Option[String]
	)