package it.polimi.dima.track.model.service.fitbit

interface FitbitAuthManager {
    fun createAuthorizationUrl(): String
    suspend fun exchangeCodeForToken(code: String): FitbitOAuthToken
    suspend fun refreshToken(refreshToken: String): FitbitOAuthToken
}
