package dbtarzan.localization

case class Language(language : String)

object Languages {
    val ENGLISH = Language("english")
    val ITALIAN = Language("italian")
    val SPANISH = Language("spanish")

    val languages = List(ENGLISH, ITALIAN, SPANISH)

    val default = ENGLISH
}