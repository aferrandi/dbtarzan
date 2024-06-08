package dbtarzan.gui.config.connections

import dbtarzan.gui.util.Validation
import dbtarzan.config.connections.ConnectionData

/* verify if a connection data can be saved */
object ConnectionDataValidation
{
  private val MAXFIELDSIZE_MIN = 200

  def validate(data : ConnectionData) : List[String] =
    List(
      errorIf("Empty name", _ => data.name.isEmpty),
      errorIf("Name must be an identifier", _ => !Validation.isIdentifier(data.name)),
      errorIf("Empty url", _ => data.url.isEmpty),
      errorIf("Url cannot contain spaces", _ => Validation.containsWhitespace(data.url)),
      errorIf("Url must be in URL form", _ => !Validation.isValidJdbcURL(data.url)),
      errorIf("Empty driver", _ => data.driver.isEmpty),
      errorIf("Zero max in clause", _ => data.maxInClauseCount.exists(v => v <= 0)),
      // errorIf("Empty user", _ => data.user.isEmpty),
      // errorIf("User cannot contain spaces", _ => Validation.containsWhtitespace(data.user)),
      // errorIf("Empty password", _ => data.password.isEmpty),
      // errorIf("Password cannot contain spaces", _ => Validation.containsWhtitespace(data.password)),
      errorIf("Empty jar", _ => data.jar.isEmpty),
      errorIf("Jar cannot contain spaces", _ => Validation.containsWhitespace(data.jar)),
      errorIf("Max field size should be over "+MAXFIELDSIZE_MIN, _ => !Validation.isMoreThanOrNone(data.maxFieldSize, MAXFIELDSIZE_MIN))
    ).flatten

  private def errorIf(errorText: String, conditionForError: String => Boolean): Option[String] = {
    Some(errorText).filter(conditionForError)
  }
}