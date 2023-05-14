package it.polimi.dima.track.services.fitbit

data class FitbitOAuthConfig(
    val clientId: String = "23QZSP",
    val clientSecret: String = "1a64d4dfb1c5e4e3f865642908f8a3c2",
    val redirectUri: String,
    val authorizationUri: String = "https://www.fitbit.com/oauth2/authorize",
    val tokenUri: String = "https://api.fitbit.com/oauth2/token",
    val codeVerifier: String,
    val codeChallenge: String,
    val scope: String = "activity profile",
    val grantType: String = "authorization_code"
)
