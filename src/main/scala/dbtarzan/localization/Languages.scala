package dbtarzan.localization

case class Language(language : String)

object Languages {
    val ENGLISH: Language = Language("english")
    val ITALIAN: Language = Language("italian")
    val SPANISH: Language = Language("spanish")

    val languages: List[Language] = List(ENGLISH, ITALIAN, SPANISH)

    val default = ENGLISH
}