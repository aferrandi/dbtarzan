package dbtarzan.gui.config.composite

import dbtarzan.db.Composite
import dbtarzan.gui.util.Validation

/* verify if a connection data can be saved */
object CompositeValidation
{
  def validate(composite : Composite) : List[String] =
    List(
      errorIf("Empty id", _ => composite.compositeId.compositeName.isEmpty),
      errorIf("Name must be an identifier", _ => !Validation.isIdentifier(composite.compositeId.compositeName)),
      errorIf("No database ids", _ => composite.databaseIds.isEmpty),
    ).flatten

  private def errorIf(errorText: String, conditionForError: String => Boolean): Option[String] = {
    Some(errorText).filter(conditionForError)
  }
}