package dbtarzan.config.global

import dbtarzan.localization.Language
import dbtarzan.config.VerificationKey

case class EncryptionData(
	verificationKey : VerificationKey
)

/* global configuration */
case class GlobalData(
	/* the laguage used for the UI */
	language : Language,
	/* if this is not empty, a passwrod is needed */
	encryptionData : Option[EncryptionData]
)