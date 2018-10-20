package dbtarzan.gui

import org.scalatest.FlatSpec

import dbtarzan.db._

class TableColumnsHeadingsTest extends FlatSpec {
  "adding primary keys to the headings" should "return a primary key text" in {
    val headings = new TableColumnsHeadings(List(Field("firstName", FieldType.STRING), Field("lastName", FieldType.STRING) ));
    val texts = headings.addPrimaryKeys(PrimaryKeys(List(PrimaryKey("nameKey", List("lastName")))))
	  assert(List(HeadingText(1, "lastName", Some(TableColumnsHeadings.PRIMARYKEY_ICON))) === texts)
  }
 "adding foreign keys to the headings" should "return a foreign key text" in {
    val headings = new TableColumnsHeadings(List(Field("firstName", FieldType.STRING), Field("lastName", FieldType.STRING) ));
    val texts = headings.addForeignKeys(ForeignKeys(List(
      ForeignKey("firstNameKey", FieldsOnTable("user", List("firstName")), FieldsOnTable("game", List("firstName")), ForeignKeyDirection.TURNED),
      ForeignKey("lastNameKey", FieldsOnTable("user", List("lastName")), FieldsOnTable("class", List("lastName")), ForeignKeyDirection.STRAIGHT)
      )))
	  assert(List(HeadingText(1, "lastName", Some(TableColumnsHeadings.FOREIGNKEY_ICON))) === texts)
  }
 "adding primary and foreign keys to the headings" should "return a primary key + foreign key text" in {
    val headings = new TableColumnsHeadings(List(Field("firstName", FieldType.STRING), Field("lastName", FieldType.STRING) ));
    headings.addForeignKeys(ForeignKeys(List(
      ForeignKey("lastNameKey", FieldsOnTable("user", List("lastName")), FieldsOnTable("class", List("lastName")), ForeignKeyDirection.STRAIGHT)
      )))
    val texts = headings.addPrimaryKeys(PrimaryKeys(List(PrimaryKey("nameKey", List("lastName")))))
	  assert(List(HeadingText(1, "lastName", Some(TableColumnsHeadings.BOTHKEYS_ICON))) === texts)
  }
 "adding foreign and primary keys to the headings" should "return a primary key + foreign key text" in {
    val headings = new TableColumnsHeadings(List(Field("firstName", FieldType.STRING), Field("lastName", FieldType.STRING) ));
    headings.addPrimaryKeys(PrimaryKeys(List(PrimaryKey("nameKey", List("lastName")))))
    val texts = headings.addForeignKeys(ForeignKeys(List(
      ForeignKey("lastNameKey", FieldsOnTable("user", List("lastName")), FieldsOnTable("class", List("lastName")), ForeignKeyDirection.STRAIGHT)
      )))
	  assert(List(HeadingText(1, "lastName", Some(TableColumnsHeadings.BOTHKEYS_ICON))) === texts)
  }
}