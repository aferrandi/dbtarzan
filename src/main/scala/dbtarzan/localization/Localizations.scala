package dbtarzan.localization

object Localizations
{
    def of(language : Language) : Localization = 
        language match {
            case Languages.ENGLISH => new English()
            case Languages.ITALIAN => new Italian()
            case Languages.SPANISH => new Spanish()
            case _  => new English()
        }  
}