package dbtarzan.db

import dbtarzan.messages.{QueryRows, QueryForeignKeys, TableId}

/**
	Represents the table of a database 
*/
class Table private (description : TableDescription, columns : Fields, foreignConstraint : Option[ForeignKeyCriteria], additionalConstraint : Option[Constraint]) {
	val sql = buildSql()

	def buildSql() : String = { 
		var foreignClosure = foreignConstraint.map(ForeignKeyTextBuilder.buildClause(_))
		"select * from " + description.name +
			foreignClosure.map(" WHERE (\n"+_+")").getOrElse("") +
			additionalConstraint.map(" AND (\n"+_.text+")").getOrElse("")
	}


	def tableDescription = description

	def columnNames = columns.fields

	def hasConstraint = additionalConstraint.isDefined
	
	private def addConstraintToExisting(constraint : Constraint) = 
		additionalConstraint.map("(" + _.text + ")\nAND (" + constraint.text + ")").map(Constraint(_))
			.getOrElse(constraint)

	def withAdditionalConstraint(constraint : Constraint) =
		new Table(description, columns, foreignConstraint, Some(addConstraintToExisting(constraint)))
}

object Table {
	def build(description : TableDescription, columns : Fields, foreignConstraint : Option[ForeignKeyCriteria], additionalConstraint : Option[Constraint]) : Table =
		new Table(description, columns, foreignConstraint, additionalConstraint)  

	def build(description : TableDescription, columns : Fields) : Table =
		build(description, columns, None, None)
}