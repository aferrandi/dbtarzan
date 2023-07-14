package dbtarzan.gui.table


import dbtarzan.db._
import dbtarzan.gui.table.headings.TableColumnsIcons
import org.scalatest.flatspec.AnyFlatSpec
import dbtarzan.testutil.TestDatabaseIds

class TableColumnsHeadingsTest extends AnyFlatSpec {

  "adding primary keys to the headings" should "return a primary key text" in {
    val headings = new TableColumnsHeadings(List(Field("firstName", FieldType.STRING, ""), Field("lastName", FieldType.STRING, "") ));
    val texts = headings.addPrimaryKeys(PrimaryKeys(List(PrimaryKey("nameKey", List("lastName")))))
	  assert(List(HeadingTextAndIcon(1, "lastName", Some(TableColumnsIcons.PRIMARYKEY_ICON))) === texts)
  }
 "adding foreign keys to the headings" should "return a foreign key text" in {
    val headings = new TableColumnsHeadings(List(Field("firstName", FieldType.STRING, ""), Field("lastName", FieldType.STRING, "") ));
    val texts = headings.addForeignKeys(ForeignKeys(List(
      ForeignKey("firstNameKey", FieldsOnTable(TestDatabaseIds.simpleTableId("user"), List("firstName")), FieldsOnTable(TestDatabaseIds.simpleTableId("game"), List("firstName")), ForeignKeyDirection.TURNED),
      ForeignKey("lastNameKey", FieldsOnTable(TestDatabaseIds.simpleTableId("user"), List("lastName")), FieldsOnTable(TestDatabaseIds.simpleTableId("class"), List("lastName")), ForeignKeyDirection.STRAIGHT)
      )))
	  assert(List(HeadingTextAndIcon(1, "lastName", Some(TableColumnsIcons.FOREIGNKEY_ICON))) === texts)
  }
 "adding primary and foreign keys to the headings" should "return a primary key + foreign key text" in {
    val headings = new TableColumnsHeadings(List(Field("firstName", FieldType.STRING, ""), Field("lastName", FieldType.STRING, "") ));
    headings.addForeignKeys(ForeignKeys(List(
      ForeignKey("lastNameKey", FieldsOnTable(TestDatabaseIds.simpleTableId("user"), List("lastName")), FieldsOnTable(TestDatabaseIds.simpleTableId("class"), List("lastName")), ForeignKeyDirection.STRAIGHT)
      )))
    val texts = headings.addPrimaryKeys(PrimaryKeys(List(PrimaryKey("nameKey", List("lastName")))))
	  assert(List(HeadingTextAndIcon(1, "lastName", Some(TableColumnsIcons.BOTHKEYS_ICON))) === texts)
  }
 "adding foreign and primary keys to the headings" should "return a primary key + foreign key text" in {
    val headings = new TableColumnsHeadings(List(Field("firstName", FieldType.STRING, ""), Field("lastName", FieldType.STRING, "") ));
    headings.addPrimaryKeys(PrimaryKeys(List(PrimaryKey("nameKey", List("lastName")))))
    val texts = headings.addForeignKeys(ForeignKeys(List(
      ForeignKey("lastNameKey", FieldsOnTable(TestDatabaseIds.simpleTableId("user"), List("lastName")), FieldsOnTable(TestDatabaseIds.simpleTableId("class"), List("lastName")), ForeignKeyDirection.STRAIGHT)
      )))
	  assert(List(HeadingTextAndIcon(1, "lastName", Some(TableColumnsIcons.BOTHKEYS_ICON))) === texts)
  }
}