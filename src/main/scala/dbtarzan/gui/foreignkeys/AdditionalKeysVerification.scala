package dbtarzan.gui.foreignkeys

import dbtarzan.db.AdditionalForeignKey
import scala.collection.immutable.HashSet

case class AdditionalKeysVerificationResult(
    nameEmpty: Boolean, 
    nameNewRow: Boolean, 
    noColumns: List[String], 
    sameColumns: List[String], 
    differentColumnsNumber: List[String], 
    nameDuplicates: List[String], 
    relationDuplicates: List[String]
    ) {
    def correct : Boolean = 
        !nameEmpty && 
        !nameNewRow && 
        noColumns.isEmpty && 
        sameColumns.isEmpty && 
        differentColumnsNumber.isEmpty &&
        nameDuplicates.isEmpty && 
        relationDuplicates.isEmpty
}

/* checks for additional foreign keys problems before saving them, included duplications and missing data */
class AdditionalKeysVerification(keys : List[AdditionalForeignKey]) {
    private def nameEmpty() : Boolean = 
        keys.map(_.name).exists(_.trim.isEmpty)

    private def nameNewRow() : Boolean = 
        keys.map(_.name).exists(_.trim == ForeignKeysTable.newRowName)

    private def singleKeyNoColumns(key : AdditionalForeignKey) : Boolean = 
        key.from.fields.isEmpty || key.to.fields.isEmpty

    private def noColumns() : List[String] = 
        keys.filter(singleKeyNoColumns(_)).map(_.name)

    private def sameColumns() : List[String] = 
        keys.filter(k => k.from == k.to).map(_.name)

    private def differentColumnsNumber() : List[String] = 
        keys.filter(k => !singleKeyNoColumns(k) && k.from.fields.size != k.to.fields.size).map(_.name)

    private def nameDuplicates() : List[String] = 
        keys.map(_.name).groupBy(identity).view.mapValues(_.size).filter({case (_, s) => s > 1}).keys.toList
    
    private def relationDuplicates(): List[String] =
        keys.groupBy(k => HashSet(k.from, k.to)).values.filter(_.size > 1).map(_.head.name).toList

    def verify(): AdditionalKeysVerificationResult = AdditionalKeysVerificationResult(
            nameEmpty(), 
            nameNewRow(), 
            noColumns(), 
            sameColumns(), 
            differentColumnsNumber(), 
            nameDuplicates(), 
            relationDuplicates()
        )    
}

object AdditionalKeysVerification {
    def verify(keys : List[AdditionalForeignKey]): AdditionalKeysVerificationResult = new AdditionalKeysVerification(keys).verify()
}

