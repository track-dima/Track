package it.polimi.dima.track.model.service.fitbit

object FitbitConfig {
  const val clientId: String = "23QZSP"
  const val clientSecret: String = "1a64d4dfb1c5e4e3f865642908f8a3c2"
  const val tokenUri: String = "https://api.fitbit.com/oauth2/token"
  const val scope: String = "activity profile"
  const val grantType: String = "authorization_code"
}
