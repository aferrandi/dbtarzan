package dbtarzan.gui.foreignkeys

import dbtarzan.db.AdditionalForeignKey
import scala.collection.immutable.HashSet

case class AdditionalKeysVerificationResult(nameDuplicates: List[String], relationDuplicates: List[String]) {
    def correct : Boolean = nameDuplicates.isEmpty && relationDuplicates.isEmpty
}

/* checks for additional foreign keys duplications */
class AdditionalKeysVerification(keys : List[AdditionalForeignKey]) {
    private def nameDuplicates() : List[String] = 
        keys.map(_.name).groupBy(identity).mapValues(_.size).filter({case (n, s) => s > 1}).keys.toList
    
    private def relationDuplicates(): List[String] =
        keys.groupBy(k => HashSet(k.from, k.to)).values.filter(_.size > 1).map(_.head.name).toList

    def verify() = AdditionalKeysVerificationResult(
                     nameDuplicates(), relationDuplicates()
                    )    
}

object AdditionalKeysVerification {
    def verify(keys : List[AdditionalForeignKey]) = new AdditionalKeysVerification(keys).verify()
}

