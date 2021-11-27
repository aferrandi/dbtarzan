package dbtarzan.localization

import org.scalatest.flatspec.AnyFlatSpec


class LocalizationsTest extends AnyFlatSpec {
  "the translations in different languages" should "be different" in {
      assert(Localizations.of(Languages.ITALIAN).help !== Localizations.of(Languages.ENGLISH).help)
      assert(Localizations.of(Languages.SPANISH).help !== Localizations.of(Languages.ENGLISH).help)
  }
}