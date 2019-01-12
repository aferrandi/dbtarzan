package dbtarzan.gui.config.connections

import dbtarzan.gui.util.Validation
import dbtarzan.config.connections.ConnectionData

/* verify if a connection data can be saved */
object ConnectionDataValidation
{
	  def validate(data : ConnectionData) : List[String] = 
    List(
      Some("Empty name").filter(_ => data.name.isEmpty),
      Some("Name cannot contain spaces").filter(_ => Validation.containsWhtitespace(data.name)), 
      Some("Empty url").filter(_ => data.url.isEmpty),
      Some("Url cannot contain spaces").filter(_ => Validation.containsWhtitespace(data.url)), 
      Some("Url must be in URL form").filter(_ => Validation.isValidURL(data.url)), 
      Some("Empty driver").filter(_ => data.driver.isEmpty),
      // Some("Empty user").filter(_ => data.user.isEmpty),
      // Some("User cannot contain spaces").filter(_ => Validation.containsWhtitespace(data.user)),
      // Some("Empty password").filter(_ => data.password.isEmpty),
      // Some("Password cannot contain spaces").filter(_ => Validation.containsWhtitespace(data.password)),
      Some("Empty jar").filter(_ => data.jar.isEmpty),
      Some("Jar cannot contain spaces").filter(_ => Validation.containsWhtitespace(data.jar))
    ).flatten 
}