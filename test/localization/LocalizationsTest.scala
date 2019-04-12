package dbtarzan.localization

import org.scalatest.FlatSpec


class LocalizationsTest extends FlatSpec {
  "the translations in different languages" should "be different" in {
      assert(Localizations.of(Languages.ITALIAN).help !== Localizations.of(Languages.ENGLISH).help)
      assert(Localizations.of(Languages.SPANISH).help !== Localizations.of(Languages.ENGLISH).help)
  }
}