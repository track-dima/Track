package it.polimi.dima.track.services.fitbit

data class FitbitOAuthConfig(
    val clientId: String,
    val redirectUri: String,
    val authorizationUri: String = "https://www.fitbit.com/oauth2/authorize",
    val tokenUri: String = "https://api.fitbit.com/oauth2/token",
    val codeVerifier: String,
    val codeChallenge: String,
    val scope: String = "activity profile",
    val grantType: String = "authorization_code"
)
