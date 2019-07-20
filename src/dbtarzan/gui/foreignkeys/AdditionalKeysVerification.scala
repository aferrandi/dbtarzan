package dbtarzan.gui.foreignkeys

import dbtarzan.db.AdditionalForeignKey
import scala.collection.immutable.HashSet



case class AdditionalKeysVerificationResult(nameEmpty: Boolean, nameNewRow: Boolean, noColumns: List[String], nameDuplicates: List[String], relationDuplicates: List[String]) {
    def correct : Boolean = 
        nameEmpty && 
        nameNewRow && 
        noColumns.isEmpty && 
        nameDuplicates.isEmpty && 
        relationDuplicates.isEmpty
}

/* checks for additional foreign keys duplications */
class AdditionalKeysVerification(keys : List[AdditionalForeignKey]) {
    private def nameEmpty() : Boolean = 
        keys.map(_.name).exists(_.trim.isEmpty)

    private def nameNewRow() : Boolean = 
        keys.map(_.name).exists(_.trim == ForeignKeysTable.newRowName)

    private def noColumns() : List[String] = 
        keys.filter(k => k.from.fields.isEmpty || k.to.fields.isEmpty).map(_.name)

    private def nameDuplicates() : List[String] = 
        keys.map(_.name).groupBy(identity).mapValues(_.size).filter({case (n, s) => s > 1}).keys.toList
    
    private def relationDuplicates(): List[String] =
        keys.groupBy(k => HashSet(k.from, k.to)).values.filter(_.size > 1).map(_.head.name).toList

    def verify() = AdditionalKeysVerificationResult(
                     nameEmpty(), nameNewRow(), noColumns(), nameDuplicates(), relationDuplicates()
                    )    
}

object AdditionalKeysVerification {
    def verify(keys : List[AdditionalForeignKey]) = new AdditionalKeysVerification(keys).verify()
}

