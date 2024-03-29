package it.polimi.dima.track.model.service.fitbit

interface FitbitAuthManager {
    val config: FitbitConfig
    val codeVerifier: String
    val codeChallenge: String
    fun createAuthorizationUrl(): String
    suspend fun exchangeCodeForToken(code: String): FitbitOAuthToken
    suspend fun isAccessTokenActive(accessToken: String): Boolean
    suspend fun refreshToken(refreshToken: String): FitbitOAuthToken
}
