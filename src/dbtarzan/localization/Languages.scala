package dbtarzan.localization

case class Language(language : String)

object Languages {
    val ENGLISH = Language("english")
    val ITALIAN = Language("italian")

    val languages = List(ENGLISH, ITALIAN)

    val default = ENGLISH
}