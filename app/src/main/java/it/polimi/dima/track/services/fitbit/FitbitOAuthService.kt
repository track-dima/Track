package it.polimi.dima.track.services.fitbit

interface FitbitOAuthService {
    @POST("/oauth2/token")
    @FormUrlEncoded
    suspend fun exchangeCodeForToken(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code_verifier") codeVerifier: String
    ): FitbitOAuthToken
}
